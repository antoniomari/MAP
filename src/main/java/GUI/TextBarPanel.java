package GUI;

import animation.Animation;
import characters.PlayingCharacter;
import graphics.SpriteManager;
import items.PickupableItem;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class TextBarPanel extends JLayeredPane
{
    // Path immagini e json //
    private final static String TEXT_BAR_PATH = "/img/barra di testo/text bar.png";

    // Immagini png (da caricare) //
    private final static BufferedImage TEXT_BAR_IMAGE;


    // Livelli del LayeredPane inventario //
    private final static Integer BAR_LEVEL = 1;
    private final static Integer SELECTION_LEVEL = 2;
    private final static Integer ITEM_LEVEL = 3;


    //**************************************************
    //            VARIABILI DI ISTANZA
    //**************************************************

    // Componenti swing //
    public JLabel barLabel;
    private JLabel textLabel;

    private Icon barIcon;

    /** Fattore di riscalamento per tutte le icone */
    private final double scalingFactor;


    static
    {
        // Caricamento immagini barra, bottoni e cella di selezione
        TEXT_BAR_IMAGE = SpriteManager.loadSpriteSheet(TEXT_BAR_PATH);
    }


    public TextBarPanel(double scalingFactor)
    {
        super();

        final int BLOCK_SIZE = 48;

        // lo scalingFactor è il rapporto tra l'altezza del menu e quella delle icone originali
        this.scalingFactor = scalingFactor;

         int x_offset = (int)(4 * BLOCK_SIZE * scalingFactor); // TODO : aggiustare questi
         int y_offset = (int)(7 * scalingFactor);

         barIcon = SpriteManager.rescaledImageIcon(TEXT_BAR_IMAGE, scalingFactor);

         barLabel = new JLabel();

         barLabel.setBounds(0, 0,
                            barIcon.getIconWidth(),
                            barIcon.getIconHeight());

         setPreferredSize(new Dimension(barIcon.getIconWidth(),
                                        barIcon.getIconHeight()));

         add(barLabel, BAR_LEVEL);
    }

    public void showTextBar(String text)
    {
        barLabel.setIcon(barIcon);
    }

    public void hideTextBar()
    {
        barLabel.setIcon(null);
    }

}
