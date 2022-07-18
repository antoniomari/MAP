package entity.items;

import java.awt.*;
import java.util.List;

public interface Triggerable
{
    void trigger();

    List<Image> getTriggerFrames(); //animazione
    Image getTriggeredSprite();
    void setTriggeredSprite();


    void isTriggered();
}
