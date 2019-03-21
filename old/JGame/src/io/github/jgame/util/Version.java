package io.github.jgame.util;

import java.io.Serializable;

import static io.github.jgame.util.StringManager.fmt;

/**
 * Quick method for quickly comparing and displaying versions..
 */
public class Version implements Serializable {
    /**
     * The string format used to display the string. Formatted using 3 arguments:
     * <p>
     * fmt (format, major, minor, micro)
     */
    public transient String format;

    /**
     * The most significant digit. Should contain a different value in production, beta, and
     * alpha releases.
     */
    public transient byte modeValue;

    /**
     * The major value. Most significant digit after the {@link #modeValue}
     */
    public transient byte major;

    /**
     * The minor value. Less significant than {@link #major}
     */
    public transient byte minor;

    /**
     * The micro value. Least significant digit.
     */
    public transient byte micro;

    /**
     * The integer value of the version
     */
    public long absoluteValue;

    /**
     * Create new version!
     *
     * @param mode The format string
     * @param mValue Release mode.
     * @param majorValue Major version
     * @param minorValue Minor version
     * @param microValue Micro version
     */
    public Version(String mode, int mValue, int majorValue, int minorValue, int microValue) {
        format = mode;
        modeValue = (byte) mValue;

        major = (byte) majorValue;
        minor = (byte) minorValue;
        micro = (byte) microValue;

        absoluteValue = getValue();
    }

    /**
     * Create new version! (from bytes directly)
     *
     * @param mode The format string
     * @param mValue Release mode
     * @param majorValue Major version
     * @param minorValue Minor version
     * @param microValue Micro version
     */
    public Version(String mode, byte mValue, byte majorValue, byte minorValue, byte microValue) {
        format = mode;
        modeValue = mValue;

        major = majorValue;
        minor = minorValue;
        micro = microValue;

        absoluteValue = getValue();
    }

    /**
     * Create a version with uninitialized values.
     */
    public Version() {
        format = "%s.%s.%s";
        modeValue = -1;
        major = -1;
        minor = -1;
        micro = -1;
    }

    /**
     * Load a version from the integer value
     *
     * @param abs Integer value
     * @return Version object
     */
    public static Version fromAbsolute(long abs) {
        byte microVal = (byte) (abs % 256d);
        abs /= 256d;
        byte minorVal = (byte) (abs % 256d);
        abs /= 256d;
        byte majorVal = (byte) (abs % 256d);
        abs /= 256;
        return new Version("%s", (byte) (abs % 256d), majorVal, minorVal, microVal);
    }

    /**
     * Get the unsigned integer of the byte. For some reason bytes are signed in java. WHY?
     *
     * @param b Original byte value
     * @return Unsigned value
     */
    public static int unsigned(byte b) {
        return b < 0 ? b + 256 : b;
    }

    /**
     * Get the display string for this version
     *
     * @return Display string
     */
    @Override
    public String toString() {
        return fmt(format, unsigned(major), unsigned(minor), unsigned(micro));
    }

    /**
     * Get the integer value for this version.
     * Later versions have a higher value. For example, 10.0.1 would be bigger than 9.8.7
     *
     * @return integer value.
     */
    public long getValue() {
        long ret = unsigned(micro);
        ret += unsigned(minor) * 256;
        ret += unsigned(major) * 65536;
        ret += unsigned(modeValue) * 16777216;
        return ret;
    }
}
