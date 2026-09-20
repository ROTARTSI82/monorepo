import java.util.Scanner;

/**
 * Represents a yarn object with various yarn-specific features such as
 * weight, quantity, colorway, brand, and name.
 */
public class YarnExample implements Comparable<YarnExample> {
    public static final String[] WEIGHTS = {"lace", "sock", "sport", "dk", 
        "worsted", "aran", "bulky", "super bulky", "jumbo"};

    private final String weight;
    private final double quantity; // in meters
    private final String colorway;
    private final String brand;
    private final String name;

    /**
     * Constructs a Yarn object with the specified weight, quantity, colorway,
     * brand, and name.
     * @param weight - the weight of the yarn
     * @param quantity - the quantity of the yarn in meters
     * @param colorway - the colorway of the yarn
     * @param brand - the brand of the yarn
     * @param name - the name of the yarn
     * @throws IllegalArgumentException if quantity is <= 0 or if weight is not contained within
     *                                  the WEIGHTS array.
     */
    public YarnExample(String weight, double quantity, String colorway, String brand, String name) {
        if (quantity <= 0.0) {
            throw new IllegalArgumentException("Quantity must be greater than 0!");
        } 
        this.weight = weight.toLowerCase();  // normalize casing for weights
        if (!YarnExample.checkWeight(weight)) {
            throw new IllegalArgumentException("Invalid weight: " + weight 
                                                + " is not present in WEIGHTS");
        }
        this.quantity = quantity;
        this.colorway = colorway;
        this.brand = brand;
        this.name = name;
    }

    /**
     * Constructs a Yarn object by copying the properties of another Yarn object.
     * Assumes that the inputted yarn is not null.
     * @param yarn - the Yarn object to copy
     */
    public YarnExample(YarnExample yarn) {
        this(yarn.weight, yarn.quantity, yarn.colorway, 
                yarn.brand, yarn.name);
    }

    /**
     * Prompts the user for input to create a new Yarn object.
     * @param input the Scanner object used to read user input.
     *              Assumes that the inputted scanner is not null
     * @return a new Yarn with the provided attributes from user input
     */
    public static YarnExample parse(Scanner input) {
        System.out.print("What is the weight of your yarn? ");
        String weight = input.nextLine();

        System.out.print("What is the quantity of your yarn? ");
        double quantity = Double.parseDouble(input.nextLine());

        System.out.print("What is the colorway of your yarn? ");
        String colorway = input.nextLine();

        System.out.print("What is the brand of your yarn? ");
        String brand = input.nextLine();

        System.out.print("What is the name of your yarn? ");
        String name = input.nextLine();

        return new YarnExample(weight, quantity, colorway, brand, name);
    }

    /**
     * Checks if the inputted weight is a valid type of yarn weight
     * @param weight - the weight to check
     * @return true if the weight is valid, false otherwise
     *  */
    public static boolean checkWeight(String weight) {
        for (String nextWeight : WEIGHTS) {
            if (nextWeight.equals(weight)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the index of the weight in the WEIGHTS array
     * @param weight - the weight to check. Assumes inputted weight is not null.
     * @return the index of the weight in the WEIGHTS array, or -1 if it is not found
     */
    public static int getWeightIndex(String weight) {
        weight = weight.toLowerCase();
        for (int i = 0; i < WEIGHTS.length; i++) {
            if (WEIGHTS[i].equals(weight)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the type of yarn weight
     * @return the type of yarn weight
     */
    public String getWeight() {
        return this.weight;
    }

    /**
     * Returns a String representation of the Yarn object
     * @return a String representation of the Yarn object
     */
    public String toString() {
        return this.brand + " " + this.name + 
                "(" + this.weight + ", " + this.quantity + ", " + this.colorway + ")";
    }

    /**
     * Compares this Yarn object to another Yarn object by descending weight,
     * ascending brand (lexicographically), descending quantity, ascending name
     * (lexicographically), then ascending colorway (lexicographically).
     * @param other - the Yarn object to compare to
     * @return a negative integer, zero, or a positive integer as this object is 
     * less than, equal to, or greater than the specified object respectively
     */
    public int compareTo(YarnExample other) {
        if (!this.weight.equals(other.weight)) {
            return YarnExample.getWeightIndex(other.weight) - 
                    YarnExample.getWeightIndex(this.weight);
        } else if (!this.brand.equals(other.brand)) {
            return this.brand.compareTo(other.brand);
        } else if (this.quantity != other.quantity) {
            return Double.compare(other.quantity, this.quantity);
        }else if (!this.name.equals(other.name)) {
            return this.name.compareTo(other.name);    
        } else {
            return this.colorway.compareTo(other.colorway);
        }
    }

    /**
     * Checks if this Yarn object is equal to another Yarn object
     * @param o - the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof YarnExample) {
            YarnExample otherYarn = (YarnExample) o;
            return this.weight.equals(otherYarn.weight)
                    && this.quantity == otherYarn.quantity
                    && this.brand.equals(otherYarn.brand) 
                    && this.name.equals(otherYarn.name) 
                    && this.colorway.equals(otherYarn.colorway); 
                   
        } else {
            return false;
        }
    }

    /**
     * Returns a hash code value for this Yarn object
     * @return a hash code value for this Yarn object
     */
    public int hashCode() {
        return 31 * this.weight.hashCode() + 31 * (this.brand.hashCode() 
                + 31 * (this.name.hashCode() + 31 * this.colorway.hashCode()));
    }
}

