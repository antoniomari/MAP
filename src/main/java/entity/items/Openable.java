package entity.items;

import animation.StillAnimation;

import java.awt.*;
import java.util.List;

public interface Openable
{
    void open();
    StillAnimation getOpenAnimation();

    void close();
    StillAnimation getCloseAnimation();

    boolean isOpen();
}
