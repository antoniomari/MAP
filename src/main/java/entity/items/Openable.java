package entity.items;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface Openable
{

    void open();

    void close();

    List<Image> getOpenFrames();

    List<Image> getCloseFrames();

    boolean isOpen();

    void loadOpenScenarios(Map<String, String> openScenarios);
}
