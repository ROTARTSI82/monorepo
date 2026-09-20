import java.util.ArrayList;
/**
 * old mac donald sings very well very good yay
 * @author Rohan Ramkumar, Grant Yang
 * @version 2021.10.14
 *
 */
public class OldMacDonaldsFarm
{
	/**
	 * name of farmer
	 */
    private String farmerName;
    /**
     * list of animals
     */
    private ArrayList<Animal> farmAnimals;
    /**
     * Default constructor
     * @postcondition farmerName is "Old Mac Donald", farmAnimals is initialized
     */
    public OldMacDonaldsFarm()
    {
        farmerName = "Old Mac Donald";
        farmAnimals = new ArrayList<>();
    }
    /**
     * sings a chicken, chick, rooster, pig
     * @param args nothing
     */
    public static void main (String [ ] args)
    {
        OldMacDonaldsFarm singer = new OldMacDonaldsFarm( );
        singer.farmAnimals.add(new Chicken( ));
        singer.singVerse( );
        singer.farmAnimals.add(new Chick());
        singer.singVerse( );
        singer.farmAnimals.add(new Rooster( ));
        singer.singVerse( );
        singer.farmAnimals.add(new Pig( ));
        singer.singVerse( );
    }
    /**
     * prints out fun things for all the animals
     */
    public void singVerse()
    {
        String phrase1 = farmerName + " had a farm," ;
        String ei = " E-I-E-I-O";
        System.out.printf("%s %s and on his farm he had some %ss,%s.\n",
                phrase1, ei, farmAnimals.get(farmAnimals.size() - 1).getCommonName(), ei);

        for (int i = farmAnimals.size() - 1; i >= 0; i--)
        {
            String speak = farmAnimals.get(i).speak();
            System.out.printf("With a %s-%s here, and a %s-%s there,\n",
                    speak, speak, speak, speak);
            System.out.printf("Here a %s, there a %s, every where a %s-%s,\n",
                    speak, speak, speak, speak);
        }


        System.out.printf("%s %s\n\n", phrase1, ei);
    }
}
