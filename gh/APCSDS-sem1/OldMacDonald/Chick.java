/**
 * This is a Chick that comes from chicken
 * @author Rohan Ramkumar
 * @version 2021.10.14
 */
public class Chick extends Chicken
{
    /**
     * Creates new Chick with type Chicken
     */
    public Chick()
    {
        super("chick");
    }
    /**
     * Constructs a new Chick of a specific type
     * @param type The type of rooster to construct
     */
    public Chick(String type)
    {
        super(type);
    }
    /**
     * @return peep
     * Peeps
     */
    public String speak()
    {
        return "peep";
    }
}
