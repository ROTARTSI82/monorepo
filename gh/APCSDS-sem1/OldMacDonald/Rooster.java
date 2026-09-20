/**
 * A Rooster is a type of chicken who says "cock-a-doodle-doo"
 * @author Grant Yang
 * @version 2021.10.11
 */
public class Rooster extends Chicken
{

    /**
     * Constructs a new Rooster of type "chicken"
     */
    public Rooster()
    {
        super("rooster");
    }

    /**
     * Constructs a new Rooster of a specific type
     * @param type The type of rooster to construct
     */
    public Rooster(String type)
    {
        super(type);
    }

    /**
     * speaks "cock-a-doodle-doo
     * @return Always returns "cock-a-doodle-doo"
     */
    @Override
    public String speak()
    {
        return "cock-a-doodle-doo";
    }
}
