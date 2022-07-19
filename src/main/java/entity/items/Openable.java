package entity.items;

import scenarios.ActionSequence;

public interface Openable
{
    void open();
    //List<Image> getOpenFrames();

    void close();
    //List<Image> getCloseFrames();

    boolean isOpen();

    void setOpenEffect(ActionSequence effect);
}
