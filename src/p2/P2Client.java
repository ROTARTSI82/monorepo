/**
 * @author Grant Yang
 * @version 2025.11.07
 * CSE 123
 * P2: Disaster Relief
 * TA: Benoit Le
 */

import java.util.*;

// A client class for creating scenarios (list of regions) and allocating relief to said regions.
public class P2Client {
    private static final Random RAND = new Random();

    public static void main(String[] args) throws Exception {
        List<Region> scenario = createRandomScenario(24, 10, 100, 100, 1000);
        // List<Region> scenario = createSimpleScenario();
        System.out.println(scenario);
        
        double budget = 10000;
        Allocation allocation = allocateRelief(budget, scenario);
        printResult(allocation, budget);
    }

    /**
     * Solves the 0/1 Knapsack problem to allocate disaster relief to sites such that the
     * number of people helped is maximized with the given budget. If there is a tie
     * in the maximum number of people helped, the cheaper solution is returned.
     * @param budget The maximum amount of money to spend
     * @param sites A list of disaster regions, each consisting of a cost for helping
     *              some number of people. We do not consider partial relief of a region,
     *              and we consider our aid to be atomic.
     * @throws IllegalArgumentException if `sites` is null
     * @return An Allocation of the set of regions we should provide relief to.
     */
    public static Allocation allocateRelief(double budget, List<Region> sites) {
        if (sites == null)
            throw new IllegalArgumentException("sites cannot be null");

        // would sorting the sites from most cost-effective to least cost-effective help?

        Allocation incl = new Allocation();
        if (budget <= 0 || sites.isEmpty())
            return incl;

        // memoization might help? but I think it will probably just be slower
        // because we will like never hit the cache with `Double`s with float imprecision
        // anyways, yay 2^n algo

        if (budget >= sites.getFirst().getCost()) {
            // subList() returns a list view and doesn't copy, so this is efficient.
            Allocation alloc = allocateRelief(budget - sites.getFirst().getCost(),
                    sites.subList(1, sites.size()));
            incl = alloc.withRegion(sites.getFirst());
        }

        // we could add a heuristic to avoid considering excluding the site if the
        // cost of the remaining sites is within our budget pretty easily, but I'm lazy
        // and idk if that really helps that much anyways
        Allocation excl = allocateRelief(budget, sites.subList(1, sites.size()));
        int inclPpl = incl.totalPeople();
        int exclPpl = excl.totalPeople();

        if (inclPpl == exclPpl)
            return incl.totalCost() > excl.totalCost() ? excl : incl;
        return inclPpl > exclPpl ? incl : excl;
    }

    ///////////////////////////////////////////////////////////////////////////
    // PROVIDED HELPER METHODS - **DO NOT MODIFY ANYTHING BELOW THIS LINE!** //
    ///////////////////////////////////////////////////////////////////////////
    
    /**
    * Prints each allocation in the provided set. Useful for getting a quick overview
    * of all allocations currently in the system.
    * @param allocations Set of allocations to print
    */
    public static void printAllocations(Set<Allocation> allocations) {
        System.out.println("All Allocations:");
        for (Allocation a : allocations) {
            System.out.println("  " + a);
        }
    }

    /**
    * Prints details about a specific allocation result, including the total people
    * helped, total cost, and any leftover budget. Handy for checking if we're
    * within budget limits!
    * @param alloc The allocation to print
    * @param budget The budget to compare against
    */
    public static void printResult(Allocation alloc, double budget) {
        System.out.println("Result: ");
        System.out.println("  " + alloc);
        System.out.println("  People helped: " + alloc.totalPeople());
        System.out.printf("  Cost: $%.2f\n", alloc.totalCost());
        System.out.printf("  Unused budget: $%.2f\n", (budget - alloc.totalCost()));
    }

    /**
    * Creates a scenario with numRegions regions by randomly choosing the population 
    * and cost of each region.
    * @param numRegions Number of regions to create
    * @param minPop Minimum population per region
    * @param maxPop Maximum population per region
    * @param minCostPer Minimum cost per person
    * @param maxCostPer Maximum cost per person
    * @return A list of randomly generated regions
    */
    public static List<Region> createRandomScenario(int numRegions, int minPop, int maxPop,
                                                    double minCostPer, double maxCostPer) {
        List<Region> result = new ArrayList<>();

        for (int i = 0; i < numRegions; i++) {
            int pop = RAND.nextInt(maxPop - minPop + 1) + minPop;
            double cost = (RAND.nextDouble(maxCostPer - minCostPer) + minCostPer) * pop;
            result.add(new Region("Region #" + i, pop, round2(cost)));
        }

        return result;
    }

    /**
    * Manually creates a simple list of regions to represent a known scenario.
    * @return A simple list of regions
    */
    public static List<Region> createSimpleScenario() {
        List<Region> result = new ArrayList<>();

        result.add(new Region("Region #1", 50, 500));
        result.add(new Region("Region #2", 100, 700));
        result.add(new Region("Region #3", 60, 1000));
        result.add(new Region("Region #4", 20, 1000));
        result.add(new Region("Region #5", 200, 900));

        return result;
    }    

    /**
    * Rounds a number to two decimal places.
    * @param num The number to round
    * @return The number rounded to two decimal places
    */
    private static double round2(double num) {
        return Math.round(num * 100) / 100.0;
    }
}
