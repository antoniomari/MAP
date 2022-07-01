package items;

import javax.management.Descriptor;

public class Item implements Observable
{
    private String description;
    private static String DEFAULT_DESCRIPTION = "Un oggetto strano";
    public Item(String description)
    {
        this.description = description;
    }

    public Item()
    {
        this(DEFAULT_DESCRIPTION);
    }

    public String observe()
    {
        return this.description;
    }

}
