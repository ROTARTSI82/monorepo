/**
 * @author Rohan Ramkumar
 * This is an abstract Animal class from which all animals extend.
 * @version 2021.10.14
 */
public abstract class Animal implements Comparable
{
    private String latinName;
    private String commonName;
    public Animal(String l,String c)
    {
    	latinName=l;
    	commonName=c;
    }
    /**
     * speaks sound
     * @return The sound it makes
     */
    public abstract String speak();
    /**
     * returns common name
     * @return common name
     */
    public String getCommonName()
    {
        return commonName;
    }
    /**
     * returns latin name
     * @return latin name
     */
    public String getLatinName()
    {
        return latinName;
    }
    /**
     * sets commonName to specified value
     * @param s string to be set
     */
    public void setCommonName(String s)
    {
    	commonName=s;
    }
    /**
     * sets latinName to specified value
     * @param s string to be set
     */
    public void setLatinName(String s)
    {
    	latinName=s;
    }
    /**
     * Compares this animal's commonName to another animal's name
     * @param other Animal to be compared to
     * @throws IllegalArgumentException when compared to an object other than an Animal
     * @return 1 if this is greater than b, 0 if same, -1 if less
     */
    public int compareTo(Object other) throws IllegalArgumentException
    {
    	if(other instanceof Animal)
        {
            return commonName.compareTo(((Animal) other).getCommonName());
    	}
    	else
        {
            throw new IllegalArgumentException("Object is not an animal");
    	}
    }
}
