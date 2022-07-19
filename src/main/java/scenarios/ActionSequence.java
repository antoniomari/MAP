package scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActionSequence
{
    private final List<Runnable> actionList;
    private int index;
    private final Mode mode;
    private final String name;

    public enum Mode
    {
        INSTANT, SEQUENCE
    }


    public ActionSequence(String name, Mode mode)
    {
        Objects.requireNonNull(mode);
        Objects.requireNonNull(name);

        this.name = name;
        this.mode = mode;

        actionList = new ArrayList<>();
        index = 0;
    }

    @Override
    public String toString()
    {
        return "[== " + name + " (" + mode + ") ==]";
    }

    public Mode getMode()
    {
        return mode;
    }

    public void append(Runnable action)
    {
        actionList.add(action);
    }

    public void runAction()
    {
        if(!isConcluded())
            actionList.get(index++).run();
    }

    public void runAll()
    {
        for(Runnable r : actionList)
            r.run();
    }

    public boolean isConcluded()
    {
        return index == actionList.size();
    }
}
