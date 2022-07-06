import items.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class PopMenuManager
{
    private static JPopupMenu itemMenu;
    private static Map<Class, JPopupMenu> classMenuMap;
    private static Object selected;

    static
    {

        classMenuMap = new HashMap<>();
        itemMenu = new JPopupMenu();
        JMenuItem observe = new JMenuItem(new AbstractAction("Osserva")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println(((Item) selected).observe());
            }
        });
        itemMenu.add(observe);

        classMenuMap.put(Item.class, itemMenu);
    }

    public static void showMenu(Object o, Component invoker, int x, int y)
    {
        selected = o;
        JPopupMenu menu = getPopupMenu(o.getClass());
        menu.show(invoker, x, y);
    }

    public static JPopupMenu getPopupMenu(Class c)
    {
        return classMenuMap.get(c);
    }

}
