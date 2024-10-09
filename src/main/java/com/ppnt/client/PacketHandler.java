package com.ppnt.client;

import com.ppnt.util.PacketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PacketHandler {

    private static final int DUMMY_PACKET_ID = 1;
    private static final int CANCEL_PACKET_ID = 2;
    private static final int HEADER_SIZE = 8;

    private final Map<Integer, Packet> receivedPackets = new ConcurrentHashMap<>();
    private final Map<Integer, ScheduledFuture<?>> scheduledFutures = new ConcurrentHashMap<>();

    private final InputStream in;
    private final OutputStream out;
    private final ScheduledExecutorService scheduler;

    public PacketHandler(InputStream in, OutputStream out, ScheduledExecutorService scheduler) {
        this.in = in;
        this.out = out;
        this.scheduler = scheduler;
    }

    public Map<Integer, Packet> getReceivedPackets() {
        return receivedPackets;
    }

    public void handleIncomingPackets(InputStream in) throws IOException {
        byte[] header = new byte[HEADER_SIZE];
        in.read(header);
        ByteBuffer headerBuffer = ByteBuffer.wrap(header);

        int packetId = headerBuffer.getInt();
        int length = headerBuffer.getInt();

        byte[] body = new byte[length - HEADER_SIZE];
        in.read(body);
        ByteBuffer bodyBuffer = ByteBuffer.wrap(body);

        int packetUniqueId = bodyBuffer.getInt();
        if (packetId == DUMMY_PACKET_ID) {
            int delay = bodyBuffer.getInt();
            Packet dummy = new Packet(packetUniqueId, delay, System.currentTimeMillis());
            System.out.println("Dodavanje dummy paketa " + packetUniqueId);
            receivedPackets.put(dummy.getId(), dummy);

            ScheduledFuture<?> future = scheduleSendingPacketBack(dummy);
            scheduledFutures.put(dummy.getId(), future);
        } else if (packetId == CANCEL_PACKET_ID) {
            System.out.println("Uklanjanje paketa " + packetUniqueId);
            receivedPackets.remove(packetUniqueId);

            ScheduledFuture<?> future = scheduledFutures.remove(packetUniqueId);
            sendCancelPacket(packetUniqueId);
            if (future != null) {
                future.cancel(false);
                System.out.println("Paket " + packetUniqueId + " je otkazan.");
            }
        }
    }

    private ScheduledFuture<?> scheduleSendingPacketBack(Packet packet) {
        return scheduler.schedule(() -> {
            try {
                System.out.println("Delay za paket " + packet.getId() + " je istekao. Šalje se nazad...");
                byte[] dummyPacket = PacketFactory.createDummyPacket(packet.getId(), packet.getDelay());
                out.write(dummyPacket);
                receivedPackets.remove(packet.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, packet.getDelay(), TimeUnit.SECONDS);
    }

    private void sendCancelPacket(int packetUniqueId) throws IOException {
        byte[] cancelPacket = PacketFactory.createCancelPacket(packetUniqueId);

        out.write(cancelPacket);
        out.flush();
    }

    public void checkExpiredPackets() {
        long currentTime = System.currentTimeMillis();

        receivedPackets.values().forEach(packet -> {
            long elapsedTime = (currentTime - packet.getTimestamp()) / 1000;
            if (elapsedTime > packet.getDelay()) {
                try {
                    System.out.println("Delay za paket " + packet.getId() + " je istekao. Šalje se notifikacija...");
                    sendCancelPacket(packet.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                packet.setDelay((int) (packet.getDelay() - elapsedTime));
                scheduleSendingPacketBack(packet);
            }
        });
    }
}
