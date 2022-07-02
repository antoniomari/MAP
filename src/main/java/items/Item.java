package items;

public class Item implements Observable
{
    private String name;
    private static String DEFAULT_NAME = "Spicoli";
    private String description;
    private static String DEFAULT_DESCRIPTION = "Un oggetto strano";

    public Item(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public Item()
    {
        this(DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    public String observe()
    {
        return this.description;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return description;
    }
}
