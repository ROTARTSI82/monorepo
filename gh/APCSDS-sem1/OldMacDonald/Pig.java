/**
 * A class representing a Pig animal
 * @author Grant Yang
 * @version 2021.10.11
 */
public class Pig extends Animal
{

    /**
     * Constructs a pig with the default type, "Pig"
     */
    public Pig()
    {
        this("pig");
    }

    /**
     * Constructs a pig with a specific type
     * @param type The type of the pig
     */
    public Pig(String type)
    {
        super("suidae suinae sus", type);
    }

    /**
     * @return Always returns "oink"
     */
    @Override
    public String speak()
    {
        return "oink";
    }
}
