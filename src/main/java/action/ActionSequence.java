package action;

import java.util.ArrayList;
import java.util.List;

public class ActionSequence
{
    private final List<Runnable> actionList;


    public ActionSequence()
    {
        actionList = new ArrayList<>();
    }

    public void append(Runnable action)
    {
        actionList.add(action);
    }

    public void performActions()
    {
        for(Runnable action : actionList)
        {
            action.run();
        }
    }

}
