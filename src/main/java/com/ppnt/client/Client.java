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

    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final ScheduledExecutorService scheduler;
    private final PacketHandler packetHandler;
    private final FileManager fileManager;

    public Client() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        scheduler = Executors.newScheduledThreadPool(1);
        packetHandler = new PacketHandler(in, out, scheduler);
        fileManager = new FileManager(packetHandler);
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
