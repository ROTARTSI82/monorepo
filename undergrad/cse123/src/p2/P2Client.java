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
        List<Region> scenario = createRandomScenario(25, 10, 100, 100, 1000);
        List<Region> scenario2 = createSimpleScenario();
        System.out.println(scenario2);
        quicksort(scenario2);
        System.out.println(scenario2);

        double budget = 120000;
        Allocation allocation = allocateRelief(budget, scenario);
        printResult(allocation, budget);
    }

    /**
     * Solves the 0/1 Knapsack problem to allocate disaster relief to sites such that the
     * number of people helped is maximized within the given budget. If there is a tie
     * in the maximum number of people helped, the cheaper solution is returned.
     * @param budget The maximum amount of money to spend
     * @param sites A list of disaster regions, each with a cost for helping
     *              some number of people. We do not consider partial relief of a region,
     *              and we consider our aid to be atomic.
     * @throws IllegalArgumentException if `sites` is null
     * @return An Allocation representing the optimal set of regions we should provide relief to.
     */
    public static Allocation allocateRelief(double budget, List<Region> sites) {
        if (sites == null)
            throw new IllegalArgumentException("sites cannot be null");

        double totCost = 0;
        for (Region region : sites)
            totCost += region.getCost();

        List<Region> cpy = new ArrayList<>(sites);
        quicksort(cpy);

        return allocReliefHelper(budget, cpy, totCost, new HashMap<>());
    }

    /**
     * Sorts the list of Regions in-place by cost and then population, with the cheapest Regions
     * first. If multiple regions have the same cost, the post populous ones appear first.
     * @param ls The list of regions to sort in-place. Must not be null.
     */
    private static void quicksort(List<Region> ls) {
        // I am banned from using Collections.sort() unfortunately
        // so I get to implement this myself
        if (ls.size() > 1) {
            boolean sorted = true;
            int idx = RAND.nextInt(ls.size());
            double pivotCost = ls.get(idx).getCost();
            int pivotPop = ls.get(idx).getPopulation();

            int lo = 0;
            int hi = ls.size() - 1;
            while (lo <= hi) {
                double cost = ls.get(lo).getCost();
                int pop = ls.get(lo).getPopulation();
                if (cost < pivotCost || (cost == pivotCost && pop >= pivotPop)) {
                    sorted &= cost == pivotCost;
                    sorted &= pop == pivotPop;
                    lo++;
                } else {
                    sorted = false;
                    Region tmp = ls.get(hi);
                    ls.set(hi--, ls.get(lo));
                    ls.set(lo, tmp);
                }
            }

            if (!sorted) {
                quicksort(ls.subList(0, lo));
                quicksort(ls.subList(lo, ls.size()));
            }
        }
    }

    /**
     * Implements a recursive search to solve the 0/1 knapsack problem to allocate
     * relief to regions such that the number of people helped is maximized within a given budget.
     * If multiple solutions help the same number of people, the cheapest one is returned.
     * @param budget The maximum amount of money to spend
     * @param sites A list of disaster regions, each with a cost for helping some number of people.
     *              This parameter will be modified by this function call!
     * @param totCost The cost of providing relief to all the Regions in `sites`.
     * @param memo Cache for partial results, mapping (n, b) to the Allocation that gives the
     *             optimal solution for providing relief for the last `n` Regions in sites
     *             within the budget `b`. This parameter will be modified by this function!
     * @return An Allocation representing the optimal set of regions we should provide relief to.
     */
    private static Allocation allocReliefHelper(double budget, List<Region> sites,
                                                double totCost,
                                                Map<Map.Entry<Integer, Double>, Allocation> memo) {
        if (budget <= 0 || sites.isEmpty())
            return new Allocation();

        Map.Entry<Integer, Double> entry = new AbstractMap.SimpleEntry<>(sites.size(), budget);
        if (memo.containsKey(entry))
            return memo.get(entry);

        if (budget >= totCost) {
            Allocation incl = new Allocation();
            for (Region region : sites)
                incl = incl.withRegion(region);
            memo.put(entry, incl);
            return incl;
        }

        double firstCost = sites.getFirst().getCost();
        if (budget >= firstCost) {
            List<Region> next = sites.subList(1, sites.size());
            Allocation incl = allocReliefHelper(budget - firstCost, next,
                    totCost - firstCost, memo).withRegion(sites.getFirst());

            Allocation excl = allocReliefHelper(budget, next, totCost - firstCost, memo);
            int inclPpl = incl.totalPeople();
            int exclPpl = excl.totalPeople();

            Allocation ret;
            if (inclPpl == exclPpl)
                ret = incl.totalCost() > excl.totalCost() ? excl : incl;
            else
                ret = inclPpl > exclPpl ? incl : excl;
            memo.put(entry, ret);
            return ret;
        }

        return new Allocation();
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
