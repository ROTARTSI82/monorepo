package com.rotartsi.jgame.net;

import com.rotartsi.jgame.logging.GenericLogger;
import com.rotartsi.jgame.util.StringManager;
import com.rotartsi.jgame.util.Version;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.rotartsi.jgame.Constants.JGameStr;
import static com.rotartsi.jgame.util.StringManager.fmt;

/**
 * Data serialization and deserialization for networking.
 */
public class NetUtils {

    /**
     * The logger object used for logging.
     */
    private static Logger logger = Logger.getLogger(NetUtils.class.getName());

    /**
     * Get a string from a byte array.
     * <p>
     * NOTE: All trailing null bytes will be stripped!
     *
     * @param packet DatagramPacket
     * @return String extracted from the byte array.
     */
    public static String extractString(DatagramPacket packet) {
        return new String(packet.getData()).replaceAll("\\x00+$", "");
    }

    /**
     * Serialize data for use in TCP or UDP. The data could now be sent over the network in the form of a byte array.
     *
     * @param data        A HashMap containing data (key 'action' would be serialized using {@code actionTable})
     * @param actionTable A HashMap of actions (integer actionIDs should be 4 digits of hex. The actionID would
     *                    be split into 2 bytes and appended to the front of the returned byte array)
     * @return serialized data in the form of a byte array
     */
    public static byte[] serialize(final HashMap<String, Object> data, HashMap<String, Integer> actionTable) {
        logger.finest(JGameStr.getString("net.NetUtils.serial") + data);
        try {
            String action = (String) data.get("action");

            HashMap<String, Object> sendDat = new HashMap<>(); // Create a copy so we don't modify original HashMap
            for (String key : data.keySet()) {
                if (!key.equals("action")) {
                    sendDat.put(key, data.get(key));
                }
            }
            //sendDat.remove("action");

            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(bytesOut);
            serializer.writeObject(sendDat);
            serializer.close();

            byte[] ret = new byte[bytesOut.size() + 2];
            byte[] rawSerial = bytesOut.toByteArray();
            System.arraycopy(rawSerial, 0, ret, 2, rawSerial.length);

            if (action != null && actionTable.containsKey(action)) {
                int actionID = actionTable.get(action);
                ret[1] = (byte) (actionID % 256);
                ret[0] = (byte) ((actionID / 256) % 256);
            } else {
                logger.info(fmt(JGameStr.getString("net.NetUtils.invalidAction"), action));
            }
            return ret;
        } catch (Exception e) {
            logger.info(StringManager.fmt(JGameStr.getString("net.NetUtils.serialFail"), data.toString(),
                    GenericLogger.getStackTrace(e)));
            logger.log(Level.WARNING, fmt(JGameStr.getString("net.NetUtils.serialFail"), data.toString()), e);
        }
        return new byte[0];
    }

    /**
     * Get the the HashMap&lt;String, Object&gt; from an Object.
     * (Basically casts everything to the proper type)
     *
     * @param o The object
     * @return The HashMap
     */
    public static HashMap<String, Object> datFromObject(Object o) {
        if (o instanceof HashMap) {
            HashMap<String, Object> finalOut = new HashMap<>();
            HashMap objMap = (HashMap) o;
            for (Object key : objMap.keySet()) {
                if (key instanceof String) {
                    finalOut.put((String) key, objMap.get(key));
                } else {
                    logger.info(fmt(JGameStr.getString("net.NetUtils.hashMapKeyCorrupt"), key.toString()));
                }
            }
            return finalOut;
        } else {
            logger.info(fmt(JGameStr.getString("net.NetUtils.deserialNotMap"), o.toString()));
        }
        return null;
    }

    /**
     * Get the original HashMap from the byte array.
     *
     * @param bytes Serialized data
     * @param actionTable Table used to deserialize data. The actionID would be retrieved from the first 2 bytes
     *                    of the array. Then, we would get the original string using the actionID and the actionTable.
     * @return Original HashMap
     */
    public static HashMap<String, Object> deserialize(byte[] bytes,
                                                      HashMap<Integer, String> actionTable) {
        try {
            byte[] readBytes = new byte[bytes.length - 2];
            System.arraycopy(bytes, 2, readBytes, 0, bytes.length - 2);

            int actionID = Version.unsigned(bytes[0]) * 256 + Version.unsigned(bytes[1]);

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(readBytes));
            Object o = ois.readObject();
            ois.close();

            HashMap<String, Object> ret = datFromObject(o);
            if (ret != null) {
                ret.put("action", actionTable.get(actionID));
            }
            logger.finest(fmt(JGameStr.getString("net.NetUtils.deserialRet"), ret));
            return ret;
        } catch (Exception e) {
            logger.log(Level.WARNING, JGameStr.getString("net.NetUtils.deserialFail"), e);
        }
        return null;
    }
}
