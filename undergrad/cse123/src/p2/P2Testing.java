import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class P2Testing {

    private List<Region> scenario;
    private double totalCost;
    private static final Random RAND = new Random();

    public void setUpRandom() {
        scenario = P2Client.createRandomScenario(16, 10, 100, 10, 100);
        totalCost = 0;
        for (Region region : scenario)
            totalCost += region.getCost();
    }

    @Test
    @DisplayName("Aid All")
    public void testAidAll() {
        setUpRandom();
        double nudge = RAND.nextDouble(100);
        Allocation alloc = P2Client.allocateRelief(totalCost + nudge, scenario);

        Allocation expected = new Allocation();
        for (Region region : scenario)
            expected = expected.withRegion(region);
        assertEquals(expected, alloc);
    }

    @Test
    @DisplayName("Aid None")
    public void testAidNone() {
        setUpRandom();
        Allocation alloc = P2Client.allocateRelief(RAND.nextDouble(100), scenario);
        assertEquals(new Allocation(), alloc);
    }

    @Test
    @DisplayName("Aid Some")
    public void testAidSome() {
        scenario = P2Client.createSimpleScenario();
        Allocation alloc = P2Client.allocateRelief(2500, scenario);

        Allocation expected = new Allocation();
        expected = expected.withRegion(scenario.getLast());
        expected = expected.withRegion(scenario.get(1));
        expected = expected.withRegion(scenario.getFirst());
        assertEquals(expected, alloc);
    }
}
