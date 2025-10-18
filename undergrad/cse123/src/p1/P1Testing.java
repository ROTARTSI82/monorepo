import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class P1Testing {
    private Repository repo1;
    private Repository repo2;
    private String[] commits, commits1, commits2;

    // Occurs before each of the individual test cases
    // (creates new repos and resets commit ids)
    @BeforeEach
    public void setUp() {
        repo1 = new Repository("repo1");
        repo2 = new Repository("repo2");
        Repository.Commit.resetIds();

        // split our test commits into 2 chunks ordered chronologically,
        // so we can test commiting them to two different repos.
        commits = new String[128];
        commits1 = new String[64];
        commits2 = new String[64];
        for (int i = 0; i < commits.length; i++)
            (i >= 64 ? commits2 : commits1)[i % 64] = commits[i] = (i % 64) + " - commit" + i;
        // ^ note the hack of including i%64 in the commit name so testHistory's
        // hacky check for backwardsIndex in the commit string will succeed.
    }

    @Test
    @DisplayName("front")
    public void testFront() throws InterruptedException {
        commitAll(repo1, commits1);
        commitAll(repo2, commits2);

        testHistory(repo1, commits1.length, commits1);
        testHistory(repo2, commits2.length, commits2);

        // all of repo2 should get put in front of repo1
        repo1.synchronize(repo2);
        testHistory(repo1, commits.length, commits);
        assertEquals(0, repo2.getRepoSize());
    }

    @Test
    @DisplayName("middle")
    public void testMiddle() throws InterruptedException {
        // sync should correctly merge this random stuff
        Random random = new Random();
        for (String commit : commits) {
            (random.nextBoolean() ? repo1 : repo2).commit(commit);
            Thread.sleep(2);
        }

        repo1.synchronize(repo2);
        testHistory(repo1, commits.length, commits);
        assertEquals(0, repo2.getRepoSize());
    }

    @Test
    @DisplayName("end")
    public void testEnd() throws InterruptedException {
        commitAll(repo2, commits1);
        commitAll(repo1, commits2);
        testHistory(repo1, commits2.length, commits2);
        testHistory(repo2, commits1.length, commits1);

        // all of repo2 should get put at the end of repo1
        repo1.synchronize(repo2);
        testHistory(repo1, commits.length, commits);
        assertEquals(0, repo2.getRepoSize());
    }

    @Test
    @DisplayName("empty")
    public void testEmpty() throws InterruptedException {
        // both empty is a no-op
        repo1.synchronize(repo2);
        assertEquals(0, repo1.getRepoSize());
        assertEquals(0, repo2.getRepoSize());

        // set up repo1 with some stuff
        commitAll(repo1, commits);
        assertEquals(0, repo2.getRepoSize());
        testHistory(repo1, commits.length, commits);

        // sync from an empty repo is still no-op
        repo1.synchronize(repo2);
        assertEquals(0, repo2.getRepoSize());
        testHistory(repo1, commits.length, commits);

        // sync to an empty repo works as expected
        repo2.synchronize(repo1);
        assertEquals(0, repo1.getRepoSize());
        testHistory(repo2, commits.length, commits);
    }

    /////////////////////////////////////////////////////////////////////////////////
    // PROVIDED HELPER METHODS (You don't have to use these if you don't want to!) //
    /////////////////////////////////////////////////////////////////////////////////

    // Commits all of the provided messages into the provided repo, making sure timestamps
    // are correctly sequential (no ties). If used, make sure to include
    //      'throws InterruptedException'
    // much like we do with 'throws FileNotFoundException'. 
    // repo and messages should be non-null.
    // Example useage:
    //
    // repo1:
    //      head -> null
    // To commit the messages "one", "two", "three", "four"
    //      commitAll(repo1, new String[]{"one", "two", "three", "four"})
    // This results in the following after picture
    // repo1:
    //      head -> "four" -> "three" -> "two" -> "one" -> null
    //
    // YOU DO NOT NEED TO UNDERSTAND HOW THIS METHOD WORKS TO USE IT! (this is why documentation
    // is important!)
    public void commitAll(Repository repo, String[] messages) throws InterruptedException {
        // Commit all of the provided messages
        for (String message : messages) {
            int size = repo.getRepoSize();
            repo.commit(message);
            
            // Make sure exactly one commit was added to the repo
            assertEquals(size + 1, repo.getRepoSize(),
                         String.format("Size not correctly updated after commiting message [%s]",
                                       message));

            // Sleep to guarantee that all commits have different time stamps
            Thread.sleep(2);
        }
    }

    // Makes sure the given repositories history is correct up to 'n' commits, checking against
    // all commits made in order. repo and allCommits should be non-null.
    // Example useage:
    //
    // repo1:
    //      head -> "four" -> "three" -> "two" -> "one" -> null
    //      (Commits made in the order ["one", "two", "three", "four"])
    // To test the getHistory() method up to n=3 commits this can be done with:
    //      testHistory(repo1, 3, new String[]{"one", "two", "three", "four"})
    // Similarly, to test getHistory() up to n=4 commits you'd use:
    //      testHistory(repo1, 4, new String[]{"one", "two", "three", "four"})
    //
    // YOU DO NOT NEED TO UNDERSTAND HOW THIS METHOD WORKS TO USE IT! (this is why documentation
    // is important!)
    public void testHistory(Repository repo, int n, String[] allCommits) {
        int totalCommits = repo.getRepoSize();
        assertTrue(n <= totalCommits,
                   String.format("Provided n [%d] too big. Only [%d] commits",
                                 n, totalCommits));

        // System.out.println(repo.getHistory(n));
        String[] nCommits = repo.getHistory(n).split("\n");

        assertTrue(nCommits.length <= n,
                   String.format("getHistory(n) returned more than n [%d] commits", n));
        assertTrue(nCommits.length <= allCommits.length,
                   String.format("Not enough expected commits to check against. " +
                                 "Expected at least [%d]. Actual [%d]",
                                 n, allCommits.length));
        
        for (int i = 0; i < n; i++) {
            String commit = nCommits[i];

            // Old commit messages/ids are on the left and the more recent commit messages/ids are
            // on the right so need to traverse from right to left
            int backwardsIndex = totalCommits - 1 - i;
            String commitMessage = allCommits[backwardsIndex];

            assertTrue(commit.contains(commitMessage),
                       String.format("Commit [%s] doesn't contain expected message [%s]",
                                     commit, commitMessage));
            assertTrue(commit.contains("" + backwardsIndex),
                       String.format("Commit [%s] doesn't contain expected id [%d]",
                                     commit, backwardsIndex));
        }
    }
}
