import java.util.*;

/**
 * The Allocation class represents an unmodifiable relief solution.
 * It provides methods to retrieve the total cost and total helped population
 * of the solution. The ordering of the regions in the solution determines
 * the population that can be helped.
 */
public class Allocation {

    private Set<Region> regions;

    /**
     * Creates a new Allocation object representing the given regions.
     * @param regions the regions in the solution
     */
    private Allocation(Set<Region> regions) {
        this.regions = new HashSet<>(regions);
    }

    /**
     * Creates a new Allocation object with no regions in it.
     */
    public Allocation() {
        this.regions = new HashSet<>();
    }

    /**
     * Returns a copy of this allocation's regions.
     */
    public Set<Region> getRegions() {
        return new HashSet<>(regions);
    }

    /**
     * Returns a new Allocation with the contents of this allocation
     * and the passed in region added to it.
     * @param region to be added to the end of the new Allocation.
     * @return a new Allocation with region added to it.
     * @throws IllegalArgumentException if this Allocation does contain the region.
     */
    public Allocation withRegion(Region region) {
        if (regions.contains(region)) {
            throw new IllegalArgumentException("Allocation already contains region " + region);
        }
        Set<Region> newRegions = new HashSet<>(regions);
        newRegions.add(region);
        return new Allocation(newRegions);
    }

    /**
     * Returns a new Allocation with the contents of this allocation
     * and the passed in region removed from it.
     * @param region to be removed from the new Allocation.
     * @return a new Allocation with region removed from it.
     * @throws IllegalArgumentException if this Allocation doesn't contain the region.
     */
    public Allocation withoutRegion(Region region) {
        if (!regions.contains(region)) {
            throw new IllegalArgumentException("Allocation doesn't contain region " + region);
        }
        Set<Region> newRegions = new HashSet<>(regions);
        newRegions.remove(region);
        return new Allocation(newRegions);
    }

    /**
     * Returns the number of regions in this Allocation.
     */
    public int size() {
        return regions.size();
    }

    /**
     * Calculates and returns the total population that can be helped
     * by this Allocation.
     * @return the total population that can be helped by this Allocation.
     */
    public int totalPeople() {
        int total = 0;
        for (Region r : regions) {
            total += r.getPopulation();
        }
        return total;
    }

    /**
     * Calculates and returns the combined cost of this Allocation.
     * @return the combined cost of this Allocation.
     */
    public double totalCost() {
        double total = 0;
        for (Region loc : regions) {
            total += loc.getCost();
        }
        return total;
    }

    /**
     * Returns a String representation of an Allocation object in the format:
     * "[Region, ..., Region]" where each Region is in its string representation.
     * @return the String representation of an Allocation object
     */
    public String toString() {
        return regions.toString();
    }

    /**
     * Compares the specified object with this allocation for equality. Returns true if the
     * specified object is also an Allocation and the two Allocations have the same
     * collection of regions.
     * @param other object to be compared for equality with this allocation
     * @return true if the specified object is equal to this allocation
     */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Allocation)) {
            return false;
        }
        Allocation otherAlloc = (Allocation) other;
        return this.regions.equals(otherAlloc.getRegions());
    }

    /**
     * Returns the hash code value for this Allocation
     * @return the hash code value for this Allocation
     */
    @Override
    public int hashCode() {
        return regions.hashCode();
    }
}
