package gui;


import general.GameManager;
import graphics.SpriteManager;
import sound.SoundHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOError;
import java.io.IOException;

public class TextBarPanel extends JLayeredPane
{
    // Path risorse //
    private final static String TEXT_BAR_PATH = "/img/barra di testo/text bar.png";
    public final static String FONT_PATH = "src/main/resources/font/text font.ttf";

    // Immagini png (da caricare) //
    private final static BufferedImage TEXT_BAR_IMAGE;
    private static Font DIALOG_FONT;

    // Livelli del LayeredPane inventario //
    private final static Integer BAR_LEVEL = 1;
    private final static Integer TEXT_LEVEL = 2;

    //**************************************************
    //            VARIABILI DI ISTANZA
    //**************************************************

    // Componenti swing //
    public JLabel barLabel;
    private JTextArea textArea;
    private Icon barIcon;
    private Font dialogFont;

    /** Fattore di riscalamento per tutte le icone */
    private final double scalingFactor;

    static
    {
        // Caricamento immagini barra, bottoni e cella di selezione
        TEXT_BAR_IMAGE = SpriteManager.loadSpriteSheet(TEXT_BAR_PATH);

        // Caricamento Font
        try
        {
            DIALOG_FONT = Font.createFont(Font.TRUETYPE_FONT, new File(FONT_PATH));
        }
        catch (IOException | FontFormatException e)
        {
            // errore nel caricamento del font
            throw new IOError(e);
        }

    }

    public TextBarPanel(double scalingFactor)
    {
        super();

        // lo scalingFactor è il rapporto tra l'altezza del menu e quella delle icone originali
        this.scalingFactor = scalingFactor;

         barIcon = SpriteManager.rescaledImageIcon(TEXT_BAR_IMAGE, scalingFactor);

         barLabel = new JLabel();
         barLabel.setBounds(0, 0,
                            barIcon.getIconWidth(),
                            barIcon.getIconHeight());

         setPreferredSize(new Dimension(barIcon.getIconWidth(),
                                        barIcon.getIconHeight()));

         add(barLabel, BAR_LEVEL);


         this.textArea = new JTextArea();
         textArea.setEditable(false);
         textArea.setBackground(new Color(0,0,0,0));
         textArea.setBorder(null);
         textArea.getCaret().deinstall(textArea);
         textArea.setVisible(false);

         textArea.setFont(DIALOG_FONT.deriveFont((float)(13.5 * scalingFactor)));

         textArea.setRows(2);
         int horizontalBorder = (int) (20 * scalingFactor);
         int verticalBorder = (int) (18 * scalingFactor);
         textArea.setBounds(horizontalBorder, verticalBorder,
                              barIcon.getIconWidth() - (int)(2.5 * horizontalBorder),
                              barIcon.getIconHeight() - 2 * verticalBorder);

         add(textArea, TEXT_LEVEL);
    }

    public void showTextBar(String text)
    {
        // update gameState
        GameManager.changeState(GameManager.GameState.TEXT_BAR);

        barLabel.setIcon(barIcon);
        textArea.setText(text);
        textArea.setVisible(true);
    }

    public void hideTextBar()
    {
        barLabel.setIcon(null);
        textArea.setText(null);
        textArea.setVisible(false);

        // riproduci suono scroll bar
        SoundHandler.playWav(SoundHandler.SCROLL_BAR_PATH, SoundHandler.Mode.SOUND);
    }
}
