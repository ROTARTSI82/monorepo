/**
 * @author Grant Yang
 * @version 2025.11.28
 * CSE 123
 * C3: B(e)ST of the B(e)ST
 * TA: Benoit Le
 */

import java.util.*;

/**
 * An immutable record-like class that represents a single Email message, storing
 * the time at which it was sent, its sentiment, subject line, sender, and recipient.
 */
public class Email implements Comparable<Email> {

    private final long timestamp;
    private final int sentiment;
    private final String subject;
    private final String from;
    private final String to;

    /**
     * Compares the time at which this email was sent with the given time.
     * @param compare The UNIX timestamp (seconds) to compare against.
     * @return 0 if this email was sent at exactly the `compare` time,
     *         a positive integer if it was sent later,
     *         and a negative integer if it was sent earlier.
     */
    public int compareTime(long compare) {
        return Long.compare(timestamp, compare);
    }

    /**
     * Constructs a new Email with the specified details.
     * @param timestamp The UNIX timestamp (seconds) at which the email was sent.
     * @param sentiment The sentiment score of the email content.
     *                  Normally, it should be in the range [-100, 100], with
     *                  -100 being the most negative and 100 being the most positive sentiment.
     * @param subject The subject line of the email.
     * @param from A String of the sender of the email.
     * @param to A String of the recipient of the email.
     */
    public Email(long timestamp, int sentiment,
                 String subject, String from, String to) {
        this.timestamp = timestamp;
        this.sentiment = sentiment;
        this.subject = subject;
        this.from = from;
        this.to = to;
    }

    /**
     * Determines equality for two Emails. Two emails are equal if and only if
     * all their details match: their sentiment scores, their subjects,
     * their senders and recipients, and the times at which they were sent.
     * @param other The Email to check equality against.
     * @return true if `other` is an Email that is equal to this one, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Email)
            return ((Email) other).compareTo(this) == 0;
        return false;
    }

    /**
     * Returns a hash code value for this Email.
     * The hash code is consistent with equals.
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = Long.hashCode(timestamp);
        hash = hash * 31 + Integer.hashCode(sentiment);
        hash = hash * 31 + subject.hashCode();
        hash = hash * 31 + from.hashCode();
        hash = hash * 31 + to.hashCode();
        return hash;
    }

    /**
     * Compares this Email against another. This total ordering on Emails is
     * determined first by timestamp (later is greater), then by sentiment
     * (positive is greater), and then by lexicographical String order by subject,
     * by sender, and finally by recipient.
     * @param other The Email to compare this one against.
     * @return Zero if the Emails are equal, a positive integer if this email was greater,
     *         and a negative integer if this email was less than the other email.
     */
    @Override
    public int compareTo(Email other) {
        List<Integer> comparisons = List.of(
                Long.compare(timestamp, other.timestamp),
                Integer.compare(sentiment, other.sentiment),
                subject.compareTo(other.subject),
                from.compareTo(other.from),
                to.compareTo(other.to));
        for (Integer comparison : comparisons)
            if (comparison != 0)
                return comparison;
        return 0;
    }

    /**
     * Interactively prompts the user and constructs an Email from the Scanner input.
     * The user will be prompted for UNIX timestamp, a sentiment score,
     * the subject line, and senders and recipients.
     * @param in The Scanner to read interactive user input from.
     * @return An Email object as specified by the user
     */
    public static Email parse(Scanner in) {
        System.out.println("====== Entering Email ======");
        System.out.print("UNIX Timestamp (seconds, integer): ");
        long timestamp = Long.parseLong(in.nextLine());
        System.out.print("Sentiment ([-100, 100], integer): ");
        int sentiment = Integer.parseInt(in.nextLine());
        System.out.print("Subject (str): ");
        String subject = in.nextLine();
        System.out.print("From (str): ");
        String from = in.nextLine();
        System.out.print("To (str): ");
        String to = in.nextLine();
        return new Email(timestamp, sentiment, subject, from, to);
    }

    /**
     * Parses an Email (possibly null) from the next line of the Scanner.
     * Expects the format "timestamp ~ sentiment ~ subject ~ from ~ to".
     * Timestamp should be an integer UNIX timestamp in seconds, and sentiment
     * should be an integer in the range of [-100, 100]. The other fields are arbitrary strings,
     * but must not contain newlines or " ~ ".
     * @param in The Scanner to read from.
     * @return A new Email object parsed from the line, or null if the line starts with "null".
     */
    public static Email rawParse(Scanner in) {
        String line = in.nextLine();
        if (line.startsWith("null"))
            return null;
        String[] parts = line.split(" ~ ", -1);
        return new Email(Long.parseLong(parts[0]), Integer.parseInt(parts[1]),
                         parts[2], parts[3], parts[4]);
    }


    /**
     * Gets a human-readable string representation of this Email.
     * @return A string in the format of "{timestamp} ~ {sentiment} ~ {subject} ~ {from} ~ {to}"
     */
    public String toString() {
        return String.format("%d ~ %d ~ %s ~ %s ~ %s",
                             timestamp, sentiment, subject, from, to);
    }
}
