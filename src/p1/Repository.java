import java.util.*;
import java.text.*;

public class Repository {

    private Commit head;
    private final String name;

    public Repository(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("cannot create repo with null or empty name");
        head = null;
        this.name = name;
        /*
            Create a new, empty repository with the specified name
                If the name is null or empty, throw an IllegalArgumentException
         */
    }

    public String getRepoHead() {
        /* O(1)
            Return the ID of the current head of this repository.
             If the head is null, return null
         */
        return head == null ? null : head.id;
    }

    public int getRepoSize() {
        //     Return the number of commits in the repository.
        int acc = 0;
        for (Commit cursor = head; cursor != null; cursor = cursor.past)
            acc++;
        return acc;
    }

    public String toString() {
        /* O(1)
            Return a string representation of this repository in the following format:

        <name> - Current head: <head>

            <head> should be the result of calling toString() on the head commit.

        If there are no commits in this repository, instead return <name> - No commits
         */
        return head == null ? name + " - No commits" :
                String.format("%s - Current head: %s", name, head);
    }

    public boolean contains(String targetId) {
        /*
            Return true if the commit with ID targetId is in the repository, false if not.

    Throws an IllegalArgumentException if targetId is null

    Note that all elements are unique. Therefore, it should not continue looping unnecessarily once the element of interest is found.
         */
        if (targetId == null)
            throw new IllegalArgumentException("targetId cannot be null");
        for (Commit cursor = head; cursor != null; cursor = cursor.past)
            if (cursor.id.equals(targetId))
                return true;
        return false;
    }

    public String getHistory(int n) {
        /*
            Return a string consisting of the String representations of the most recent n commits in this repository, with the most recent first. Commits should be separated by a newline (\n) character with no trailing newline character at the end.

        If there are fewer than n commits in this repository, return them all.

        If there are no commits in this repository, return the empty string.

        If n is non-positive, throw an IllegalArgumentException.
         */
        String ret = "";
        Commit cursor = head;
        for (int i = 0; i < n && cursor != null; i++, cursor = cursor.past)
            ret += cursor.toString() + '\n';
        return ret.isEmpty() ? "" : ret.substring(0, ret.length() - 1);
    }

    public String commit(String message) {
        /* O(1)
            Create a new commit with the given message, and add it to this repository.

        The new commit should become the new head of this repository, preserving the history behind it.

    Throws an IllegalArgumentException if message is null

    Return the ID of the new commit.
         */
        head = new Commit(message, head);
        return null;
    }


    public boolean drop(String targetId) {
        /*
            Remove the commit with ID targetId from this repository, maintaining the rest of the history.

    Throws an IllegalArgumentException if targetId is null

    Returns true if the commit was successfully dropped, and false if there is no commit that matches the given ID in the repository.

    Note that all elements are unique. Therefore, it should not continue looping unnecessarily once the element of interest is found.
         */

        if (head == null)
            return false;
        if (head.id.equals(targetId)) {
            head = head.past;
            return true;
        }

        Commit cursor = head;
        while (cursor.past != null && !cursor.past.id.equals(targetId))
            cursor = cursor.past;

        if (cursor.past == null)
            return false;
        cursor.past = cursor.past.past;
        return true;
    }

    public void synchronize(Repository other) {
        /*
            Takes all the commits in the other repository and moves them into this repository, combining the two repository histories such that chronological order is preserved. That is, after executing this method, this repository should contain all commits that were from this and other, and the commits should be ordered in timestamp order from most recent to least recent.

        If the other repository is null, throw an IllegalArgumentException

        If the other repository is empty, this repository should remain unchanged.

        If this repository is empty, all commits in the other repository should be moved into this repository.

        At the end of this method's execution, other should be an empty repository in all cases.

        You should not construct any new Commit objects to implement this method. You may, however, create as many references as you like.
         */
        if (other.head != null && (head == null || other.head.timeStamp >= head.timeStamp)) {
            Commit oldHead = head;
            head = other.head;
            other.head = other.head.past;
            head.past = oldHead;
        }

        Commit cursor = head;
        if (cursor != null)
            while (other.head != null) {
                while (cursor.past != null
                        && cursor.past.timeStamp > other.head.timeStamp)
                    cursor = cursor.past;

                Commit oldPast = cursor.past;
                cursor.past = other.head;
                other.head = other.head.past;
                cursor.past.past = oldPast;
            }
    }


    /**
     * DO NOT MODIFY
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public static class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
