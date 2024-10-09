package com.ppnt.client;

import java.io.*;
import java.util.Map;

public class FileManager {

    private static final String SAVED_PACKETS_FILE = "saved_packets.dat";
    private final PacketHandler packetHandler;

    public FileManager(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public static void savePacketsToFile(Map<Integer, Packet> receivedPackets) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVED_PACKETS_FILE))) {
            oos.writeObject(receivedPackets);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPacketsFromFile() {
        File file = new File(SAVED_PACKETS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Map<Integer, Packet> packets = (Map<Integer, Packet>) ois.readObject();
                packetHandler.getReceivedPackets().putAll(packets);
                System.out.println("Paketi uspešno učitani.");
                file.delete();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Ne postoji ni jedan zaostali paket za slanje.");
        }
    }
}
