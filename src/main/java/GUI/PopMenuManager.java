package GUI;

import entity.characters.NPC;
import entity.items.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class PopMenuManager
{
    private static JPopupMenu itemMenu;
    private static JPopupMenu doorMenu;
    private static JPopupMenu pickupableItemMenu;
    private static JPopupMenu npcMenu;
    private static Map<Class, JPopupMenu> classMenuMap;
    private static Object selected;

    private final static Action OBSERVE_ACTION = new AbstractAction("Osserva")
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ((Observable) selected).observe();
        }
    };

    // TODO : migliorare nome
    private final static Action OPEN_CLOSE_ACTION = new AbstractAction("Apri/Chiudi")
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Openable opItem = ((Openable) selected);

            if(opItem.isOpen())
                opItem.close();
            else
                opItem.open();
        }
    };

    private final static Action PICKUP_ACTION = new AbstractAction("Raccogli")
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ((PickupableItem) selected).pickup();
        }
    };

    private final static Action SPEAK_ACTION = new AbstractAction("Parla")
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ((NPC) selected).speak();
        }
    };


    static
    {
        classMenuMap = new HashMap<>();
        setupItemMenu();
        setupDoorMenu();
        setupPickupableItemMenu();
        setupNPCMenu();
    }

    private static void setupItemMenu()
    {
        itemMenu = new JPopupMenu();
        itemMenu.add(new JMenuItem(OBSERVE_ACTION));
        classMenuMap.put(Item.class, itemMenu);
    }

    // TODO: aggiungere chiusura
    private static void setupDoorMenu()
    {
        doorMenu = new JPopupMenu();

        doorMenu.add(new JMenuItem(OBSERVE_ACTION));
        doorMenu.add(new JMenuItem(OPEN_CLOSE_ACTION));
        classMenuMap.put(Door.class, doorMenu);
    }

    private static void setupPickupableItemMenu()
    {
        pickupableItemMenu = new JPopupMenu();

        pickupableItemMenu.add(new JMenuItem(OBSERVE_ACTION));
        pickupableItemMenu.add(new JMenuItem(PICKUP_ACTION));
        classMenuMap.put(PickupableItem.class, pickupableItemMenu);
    }

    private static void setupNPCMenu()
    {
        npcMenu = new JPopupMenu();

        npcMenu.add(new JMenuItem(SPEAK_ACTION));
        classMenuMap.put(NPC.class, npcMenu);
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
