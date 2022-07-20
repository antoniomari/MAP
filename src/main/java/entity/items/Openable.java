package entity.items;

import general.ActionSequence;

import java.awt.*;
import java.util.List;

public interface Openable
{

    void open();

    void close();

    List<Image> getOpenFrames();

    List<Image> getCloseFrames();

    boolean isOpen();

    void setOpenEffect(ActionSequence effect);

    void setCloseEffect(ActionSequence effect);



}
