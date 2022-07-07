package GUI;

import graphics.SpriteManager;
import rooms.Coordinates;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class InventoryPanel extends JLayeredPane
{
    /*
    private final static String BACKGROUND_PATH = "/img/inventario/Barra oggetti inventario.png";
    private final static BufferedImage BACKGROUND_IMAGE;

    //
    private List<InventoryBar> inventoryBars;

    static
    {
        BACKGROUND_IMAGE = SpriteManager.loadSpriteSheet(BACKGROUND_PATH);
    }

    private class InventoryBar extends JLayeredPane
    {
        private final static int CAPACITY = 10;
        private final static int SIZE = 48;

        private int lastInserted = -1;

        List<JLabel> itemLabels;

        private final JLabel BACKGROUND_LABEL = new JLabel(new ImageIcon(BACKGROUND_IMAGE));


        InventoryBar()
        {
            super();

            Insets insets = this.getInsets();

            BACKGROUND_LABEL.setBounds(insets.left, insets.top,
                    BACKGROUND_LABEL.getIcon().getIconWidth(), BACKGROUND_LABEL.getIcon().getIconHeight());

            add(BACKGROUND_LABEL, Integer.valueOf(1));

            setPreferredSize(new Dimension(480, 48));

            itemLabels = new ArrayList<>(CAPACITY);

            for(int i = 1; i <= CAPACITY; i++)
            {
                JLabel tempLabel = new JLabel();
                Coordinates coord = calculateOffset(i-1);
                tempLabel.setBounds(insets.left + coord.getX(), insets.top +  coord.getY(),
                        SIZE, SIZE);
                itemLabels.add(tempLabel);
                add(tempLabel, Integer.valueOf(2));

            }

        }

        public Coordinates calculateOffset(int i)
        {
            return new Coordinates(i * SIZE + 1, 1);
        }

        public void setLabelIcon(Icon icon, int i)
        {
            itemLabels.get(i).setIcon(icon);
        }

        public boolean isFull()
        {
            return itemLabels.get(CAPACITY - 1).getIcon() != null;
        }
        public void add(Icon icon)
        {
            itemLabels.get(++lastInserted).setIcon(icon);
        }
    }

    public InventoryPanel()
    {
        super(new CardLayout());

        inventoryBars = new ArrayList<>(3);
        for(int i = 1; i <= 3; i++)
        {
            inventoryBars.add(new InventoryBar());
        }

        add(inventoryBars.get(0), "0");
        add(inventoryBars.get(1), "1");
        add(inventoryBars.get(2), "2");

        setPreferredSize(new Dimension(480, 48));
        ((CardLayout)getLayout()).show(this, "0");
    }

    public void addItem(Icon icon)
    {
        for(InventoryBar bar: inventoryBars)
        {
            if(!bar.isFull())
            {
                bar.add(icon);
                break;
            }
        }
    }

     */

    private final static int CAPACITY = 30;
    private final static int LABEL_SIZE = 48;
    private final static String BACKGROUND_PATH = "/img/inventario/Barra oggetti inventario.png";
    private final static BufferedImage BACKGROUND_IMAGE;

    private List<JLabel> itemLabelList;
    private List<Icon> itemIconList;

    static
    {
        BACKGROUND_IMAGE = SpriteManager.loadSpriteSheet(BACKGROUND_PATH);
    }


    public InventoryPanel()
    {
        super();

        Insets inventoryInsets = this.getInsets();
        // setup background image
        JLabel backgroundLabel = new JLabel(new ImageIcon(BACKGROUND_IMAGE));
        add(backgroundLabel, Integer.valueOf(1));

        backgroundLabel.setBounds(inventoryInsets.left, inventoryInsets.top,
                backgroundLabel.getIcon().getIconWidth(), backgroundLabel.getIcon().getIconHeight());

        setPreferredSize(new Dimension(480, 48));

        itemIconList = new ArrayList<>(CAPACITY);

        // crea 10 label e posizionale
        itemLabelList = new ArrayList<>(10);

        for(int i = 0; i < 10; i++)
        {
            JLabel tempLabel = new JLabel();
            Coordinates coord = calculateOffset(i);
            tempLabel.setBounds(inventoryInsets.left + coord.getX(),
                                inventoryInsets.top +  coord.getY(),
                                    LABEL_SIZE, LABEL_SIZE);
            // aggiungi label alla lista
            itemLabelList.add(tempLabel);
            // aggiungi label al pannello
            add(tempLabel, Integer.valueOf(2));
        }
    }

    public Coordinates calculateOffset(int i)
    {
        return new Coordinates(i * LABEL_SIZE + 1, 1);
    }

    public void addItem(Icon icon)
    {
        itemIconList.add(icon);

        //temp
        displayBar(1);
    }

    public void displayBar(int i)
    {
        //int maximum = CAPACITY / 10; TODO: controllo sul massimo

        // selezionare la sottolista
        int start = (i-1) * 10;
        int end = Math.min(start + 10, itemIconList.size());

        List<Icon> sublist = itemIconList.subList(start, end);

        // binsing di ogni elemento della sottolista nella label
        for(int j = 0; j < sublist.size(); j++)
        {
            itemLabelList.get(j).setIcon(sublist.get(j));
        }
    }
}
