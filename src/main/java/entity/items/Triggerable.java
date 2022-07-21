package entity.items;

import general.ActionSequence;

import java.awt.*;
import java.util.List;

public interface Triggerable
{
    void trigger();

    void setTriggerScenario(ActionSequence triggerScenario);

    boolean hasBeenTriggered();
}
