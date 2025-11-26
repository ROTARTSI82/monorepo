import java.util.*;

public class Email implements Comparable<Email> {

    private final long timestamp;
    private final int sentiment;
    private final String subject;
    private final String from;
    private final String to;

    public Email(long timestamp, int sentiment,
                 String subject, String from, String to) {
        this.timestamp = timestamp;
        this.sentiment = sentiment;
        this.subject = subject;
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Email otherItem)
            return otherItem.compareTo(this) == 0;
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

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

    public static Email parse(Scanner in) {
        String line = in.nextLine();
        if (line.startsWith("null"))
            return null;
        if (line.endsWith(" ~ "))
            line += " "; // hack around split()'s behavior
        String[] parts = line.split(" ~ ");
        return new Email(Long.parseLong(parts[0]), Integer.parseInt(parts[1]),
                         parts[2], parts[3], parts[4]);
    }

    public String toString() {
        return String.format("%d ~ %d ~ %s ~ %s ~ %s",
                             timestamp, sentiment, subject, from, to);
    }
}
