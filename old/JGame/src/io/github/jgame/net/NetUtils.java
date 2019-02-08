package io.github.jgame.net;

import io.github.jgame.logging.GenericLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

public class NetUtils {
    private static Logger logger = Logger.getLogger("io.github.jgame.net.NetUtils");
    public static String extractString(DatagramPacket packet) {
        return new String(packet.getData()).replace("\0", "");
    }

    public static byte[] serialize(HashMap<String, Object> data) {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(bytesOut);
            serializer.writeObject(data);
            serializer.close();
            return bytesOut.toByteArray();
        } catch (Exception e) {
            logger.info(String.format("Serialization failed: %s\n%s ", data.toString(),
                    GenericLogger.getStackTrace(e)));
        }
        return new byte[0];
    }

    public static HashMap<String, Object> datFromObject(Object o) {
        if (o instanceof HashMap) {
            HashMap<String, Object> finalOut = new HashMap<>();
            HashMap objMap = (HashMap) o;
            for (Object key : objMap.keySet()) {
                if (key instanceof String) {
                    finalOut.put((String) key, objMap.get(key));
                } else {
                    logger.info("Failed to deserialize key: " + key.toString());
                }
            }
            return finalOut;
        } else {
            logger.info("Deserialization didn't return HashMap: " + o.toString());
        }
        return null;
    }

    public static HashMap<String, Object> deserialize(byte[] bytes) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object o = ois.readObject();
            ois.close();
            return datFromObject(o);
        } catch (Exception e) {
            logger.info(String.format("Deserialization failed: %s\n%s ",
                    Arrays.toString(bytes), GenericLogger.getStackTrace(e)));
        }
        return null;
    }
}
