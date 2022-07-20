package entity.items;

public class TriggerableItem extends Item // implements Triggerable
{
    private boolean hasBeenTriggered;
    public TriggerableItem(String name, String description)
    {
        super(name, description);
        hasBeenTriggered = false;
    }

    /*
    @Override
    public void trigger()
    {

    }

     */

}
