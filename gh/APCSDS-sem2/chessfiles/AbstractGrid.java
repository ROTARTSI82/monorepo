/*
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2005-2006 Cay S. Horstmann (http://horstmann.com)
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * @author Cay Horstmann
 */

import java.util.ArrayList;

/**
 * <code>AbstractGrid</code> contains the methods that are common to grid
 * implementations. <br />
 * The implementation of this class is testable on the AP CS AB exam.
 * @author Cay Horstmann
 * @param <E> The type the grid contains
 * @version 2022.04.03
 */
public abstract class AbstractGrid<E> implements Grid<E>
{
    /**
     * Gets the neighbors of a location
     * @param loc a location in this grid
     * @return Gets a list of elements adjacent to a location, relying on
     *         getOccupiedAdjacentLocations
     */
    public ArrayList<E> getNeighbors(Location loc)
    {
        ArrayList<E> neighbors = new ArrayList<E>();
        for (Location neighborLoc : getOccupiedAdjacentLocations(loc))
            neighbors.add(get(neighborLoc));
        return neighbors;
    }

    /**
     *
     * @param loc a location in this grid
     * @return Valid adjacent locations
     */
    public ArrayList<Location> getValidAdjacentLocations(Location loc)
    {
        ArrayList<Location> locs = new ArrayList<Location>();

        int d = Location.NORTH;
        for (int i = 0; i < Location.FULL_CIRCLE / Location.HALF_RIGHT; i++)
        {
            Location neighborLoc = loc.getAdjacentLocation(d);
            if (isValid(neighborLoc))
                locs.add(neighborLoc);
            d = d + Location.HALF_RIGHT;
        }
        return locs;
    }

    /**
     * Gets all empty adjacent locations
     * @param loc a location in this grid
     * @return An array of locations
     */
    public ArrayList<Location> getEmptyAdjacentLocations(Location loc)
    {
        ArrayList<Location> locs = new ArrayList<Location>();
        for (Location neighborLoc : getValidAdjacentLocations(loc))
        {
            if (get(neighborLoc) == null)
                locs.add(neighborLoc);
        }
        return locs;
    }

    /**
     * Gets non-empty adjacent locations
     * @param loc a location in this grid
     * @return An array of Locations
     */
    public ArrayList<Location> getOccupiedAdjacentLocations(Location loc)
    {
        ArrayList<Location> locs = new ArrayList<Location>();
        for (Location neighborLoc : getValidAdjacentLocations(loc))
        {
            if (get(neighborLoc) != null)
                locs.add(neighborLoc);
        }
        return locs;
    }

    /**
     * Creates a string that describes this grid.
     * @return a string with descriptions of all objects in this grid (not
     * necessarily in any particular order), in the format {loc=obj, loc=obj,
     * ...}
     */
    public String toString()
    {
        StringBuilder s = new StringBuilder("{");
        for (Location loc : getOccupiedLocations())
        {
            if (s.length() > 1)
                s.append(", ");
            s.append(loc).append("=").append(get(loc));
        }
        return s + "}";
    }
}
