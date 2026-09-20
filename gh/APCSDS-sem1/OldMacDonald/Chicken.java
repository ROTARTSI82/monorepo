/**
 * This is a chicken animal
 * @author Grant Yang
 * @version 2021.10.11
 */
public class Chicken extends Animal
{

    /**
     * Default constructor for the chicken type of "chicken"
     */
    public Chicken()
    {
        this("chicken");
    }

    /**
     * Constructs a specific type of chicken
     * @param chickenType The type of chicken to construct
     */
    public Chicken(String chickenType)
    {
        super("Gallus Gallus domesticus", chickenType);
    }

    /**
     * @return Always returns "bawk"
     */
    @Override
    public String speak()
    {
        return "bawk";
    }
}
