package entity.items;

import general.ActionSequence;
import general.GameManager;

public class TriggerableItem extends Item implements Triggerable
{
    private boolean hasBeenTriggered;
    private boolean directlyTriggerable;
    private ActionSequence triggerScenario;

    public TriggerableItem(String name, String description, boolean directlyTriggerable)
    {
        super(name, description);
        hasBeenTriggered = false;
        this.directlyTriggerable = directlyTriggerable;
    }

    @Override
    public boolean hasBeenTriggered()
    {
        return hasBeenTriggered;
    }
    @Override
    public void setTriggerScenario(ActionSequence triggerScenario)
    {
        this.triggerScenario = triggerScenario;
    }

    @Override
    public void trigger()
    {
        GameManager.startScenario(triggerScenario);
    }

    public boolean isDirectlyTriggerable()
    {
        return directlyTriggerable;
    }

}
