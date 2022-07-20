package entity.items;

import java.awt.*;
import java.util.List;

public interface Triggerable
{
    void trigger();

    void setTriggerScenario();

    boolean hasBeenTriggered();
}
