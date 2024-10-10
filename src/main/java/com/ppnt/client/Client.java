package com.ppnt.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Client {

    private static final String SERVER_ADDRESS = "hermes.plusplus.rs";
    private static final int SERVER_PORT = 4000;

    private static final int MAX_ATTEMPTS = 5;
    private static final long RETRY_DELAY = 00;

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private ScheduledExecutorService scheduler;
    private PacketHandler packetHandler;
    private FileManager fileManager;

    public Client() {
        boolean connected = false;
        int attempts = 0;

        while (!connected && attempts < MAX_ATTEMPTS) {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                in = socket.getInputStream();
                out = socket.getOutputStream();

                scheduler = Executors.newScheduledThreadPool(1);
                packetHandler = new PacketHandler(in, out, scheduler);
                fileManager = new FileManager(packetHandler);

                connected = true;
            } catch (IOException e) {
                attempts++;
                System.err.println("Neuspelo povezivanje. Pokusaj broj: " + attempts);

                if (attempts < MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Povezivanje prekinuto.", ex);
                    }
                } else {
                    System.err.println("Povezivanje nije uspelo nakon " + MAX_ATTEMPTS + " pokušaja.");
                    throw new RuntimeException("Neuspešno povezivanje sa serverom.", e);
                }
            }
        }
    }

    public void start() {
        fileManager.loadPacketsFromFile();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            FileManager.savePacketsToFile(packetHandler.getReceivedPackets());
            System.out.println("Paketi sačuvani pre gašenja.");
            scheduler.shutdownNow();
            closeResources();
        }));

        packetHandler.checkExpiredPackets();

        try {
            while (true) {
                packetHandler.handleIncomingPackets(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
