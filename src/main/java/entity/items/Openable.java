package entity.items;

import animation.StillAnimation;

import java.awt.*;
import java.util.List;

public interface Openable
{
    void open();
    List<Image> getOpenFrames();

    void close();
    List<Image> getCloseFrames();

    boolean isOpen();
}
