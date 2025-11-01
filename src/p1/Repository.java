/**
 * @author Grant Yang
 * @version 2025.10.18
 * CSE 123
 * P1: Mini-Git
 * TA: Benoit Le
 */

import java.util.*;
import java.text.*;

/**
 * Represents a named repository in a Git-like version control system.
 * A repository is a collection of commits, where each commit represents a snapshot of changes.
 */
public class Repository {

    private Commit head;
    private final String name;

    /**
     * Constructs a new, empty repository with the specified name.
     * @param name The name for this repository. This must not be an empty string.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    public Repository(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("cannot create repo with null or empty name");
        head = null;
        this.name = name;
    }

    /**
     * Gets the ID of the current head commit of this repository. This operation is O(1).
     * @return The ID of the most recent commit, or null if the repository is empty.
     */
    public String getRepoHead() {
        return head == null ? null : head.id;
    }

    /**
     * Gets the total number of commits in the repository.
     * @return The (non-negative) number of commits.
     */
    public int getRepoSize() {
        int acc = 0;
        for (Commit cursor = head; cursor != null; cursor = cursor.past)
            acc++;
        return acc;
    }

    /**
     * Gets a human-readable string representation of this repository. This operation is O(1).
     * If the repository has commits, the format is:
     * >    "{name} - Current head: {head}"
     * where {head} is the string representation of the head commit.
     * If the repository is empty, the format is:
     * >    "{name} - No commits"
     * @return A string summarizing the repository's state.
     */
    public String toString() {
        return head == null ? name + " - No commits" :
                String.format("%s - Current head: %s", name, head);
    }

    /**
     * Checks if a commit with the specified ID exists in the repository.
     * @param targetId The ID of the commit to search for.
     * @return true if a commit with the given ID exists in this repo, false otherwise.
     * @throws IllegalArgumentException if targetId is null.
     */
    public boolean contains(String targetId) {
        if (targetId == null)
            throw new IllegalArgumentException("targetId cannot be null");
        for (Commit cursor = head; cursor != null; cursor = cursor.past)
            if (cursor.id.equals(targetId))
                return true;
        return false;
    }

    /**
     * Returns a string containing the history of the most recent n commits.
     * The commits are listed from most recent to least recent, separated by newlines.
     * No trailing newline is included.
     * @param n The maximum number of commits to include in the history.
     * @return A newline-separated string of the most recent n commits, or all commits if n is
     *         larger than the repository size. Returns an empty string if the repository is empty.
     * @throws IllegalArgumentException if n is not a positive number.
     */
    public String getHistory(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be a positive integer.");
        String ret = "";
        Commit cursor = head;
        for (int i = 0; i < n && cursor != null; i++, cursor = cursor.past)
            ret += cursor.toString() + '\n';
        return ret.isEmpty() ? "" : ret.substring(0, ret.length() - 1);
    }

    /**
     * Creates a new commit with the given message and adds it to the repository.
     * The new commit becomes the head of the repository. This operation is O(1).
     * @param message The message for the new commit.
     * @return The unique ID of the newly created commit.
     * @throws IllegalArgumentException if message is null.
     */
    public String commit(String message) {
        if (message == null)
            throw new IllegalArgumentException("message cannot be null");
        head = new Commit(message, head);
        return head.id;
    }

    /**
     * Tries to remove the commit with the specified ID from the repository.
     * @param targetId The ID of the commit to remove.
     * @return true if the commit was removed, false if it did not exist.
     * @throws IllegalArgumentException if targetId is null.
     */
    public boolean drop(String targetId) {
        if (targetId == null)
            throw new IllegalArgumentException("targetId cannot be null");

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

    /**
     * Merges the history of another repository into this one. All commits from the other
     * repository are moved into this one, and the histories are combined.
     * After this operation, the other repository will be empty.
     * @param other The repository to merge into this one.
     *              `other` must not be the same repo as the one synchronize() is called on.
     * @throws IllegalArgumentException if the other repository is null.
     */
    public void synchronize(Repository other) {
        if (other == null)
            throw new IllegalArgumentException("other repository cannot be null");
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