package io.github.jgame;

import java.io.Serializable;

import static io.github.jgame.util.StringManager.fmt;
import static io.github.jgame.util.UniversalResources.JGameStr;

public class Version implements Serializable {
    public transient String releaseMode;
    public transient byte modeValue;

    public transient byte major;
    public transient byte minor;
    public transient byte micro;

    public long absoluteValue;

    public Version(String mode, int mValue, int majorValue, int minorValue, int microValue) {
        releaseMode = mode;
        modeValue = (byte) mValue;

        major = (byte) majorValue;
        minor = (byte) minorValue;
        micro = (byte) microValue;

        absoluteValue = getValue();
    }

    public Version(String mode, byte mValue, byte majorValue, byte minorValue, byte microValue) {
        releaseMode = mode;
        modeValue = mValue;

        major = majorValue;
        minor = minorValue;
        micro = microValue;

        absoluteValue = getValue();
    }

    public Version() {
        releaseMode = "%s";
        modeValue = -1;
        major = -1;
        minor = -1;
        micro = -1;
    }

    public static Version fromAbsolute(long abs) {
        byte microVal = (byte) (abs % 256d);
        abs /= 256d;
        byte minorVal = (byte) (abs % 256d);
        abs /= 256d;
        byte majorVal = (byte) (abs % 256d);
        abs /= 256;
        return new Version("%s", (byte) (abs % 256d), majorVal, minorVal, microVal);
    }

    public static int unsigned(byte b) {
        return b < 0 ? b + 256 : b;
    }

    @Override
    public String toString() {
        return fmt(releaseMode, fmt(JGameStr.getString("Version.toStringFormat"), unsigned(major),
                unsigned(minor), unsigned(micro)));
    }

    public long getValue() {
        long ret = unsigned(micro);
        ret += unsigned(minor) * 256;
        ret += unsigned(major) * 65536;
        ret += unsigned(modeValue) * 16777216;
        return ret;
    }
}
