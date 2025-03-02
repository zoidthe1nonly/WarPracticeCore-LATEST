package dev.zoid.warpracticecore.storage;

import java.io.*;

public class SpawnData {
    private static final File file = new File("spawn.bin");

    public static void init() {
        try {
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
        } catch (IOException ignored) {}
    }

    public static void save(double x, double y, double z, float yaw, float pitch, String world) {
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            dos.writeDouble(x);
            dos.writeDouble(y);
            dos.writeDouble(z);
            dos.writeFloat(yaw);
            dos.writeFloat(pitch);
            dos.writeUTF(world);
        } catch (IOException ignored) {}
    }

    public static Object[] load() {
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            return new Object[]{dis.readDouble(), dis.readDouble(), dis.readDouble(), dis.readFloat(), dis.readFloat(), dis.readUTF()};
        } catch (IOException e) {
            return null;
        }
    }
}