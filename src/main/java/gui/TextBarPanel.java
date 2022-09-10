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

/**
 * Classe che rappresenta la barra di dialogo del gioco.
 */
public class TextBarPanel extends JLayeredPane
{
    // Path risorse //
    private final static String TEXT_BAR_PATH = "/img/barra di testo/text bar.png";
    public final static String FONT_PATH = "src/main/resources/font/text font.ttf";

    // Immagini png (da caricare) //
    /** Immagine della barra di testo. */
    private final static BufferedImage TEXT_BAR_IMAGE;
    /** */
    private static final Font DIALOG_FONT;

    // Livelli del LayeredPane inventario //
    /** Livello per la visualizzazione della barra di testo. */
    private final static Integer BAR_LEVEL = 1;
    /** Livello per la visualizzazione del testo. */
    private final static Integer TEXT_LEVEL = 2;

    //**************************************************
    //            VARIABILI DI ISTANZA
    //**************************************************

    // Componenti swing //
    /** Label della barra di testo. */
    public JLabel barLabel;
    /** Area di testo. */
    private final JTextArea textArea;
    /** Icona della barra di testo. */
    private final Icon barIcon;

    static
    {
        // Caricamento immagini barra, bottoni e cella di selezione
        TEXT_BAR_IMAGE = SpriteManager.loadImage(TEXT_BAR_PATH);

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

    /**
     * Visualizza barra di testo.
     *
     * @param text testo da visualizzare
     */
    public void showTextBar(String text)
    {
        // update gameState
        GameManager.changeState(GameManager.GameState.TEXT_BAR);

        barLabel.setIcon(barIcon);
        textArea.setText(text);
        textArea.setVisible(true);
    }

    /**
     * Nascondi barra di testo.
     */
    public void hideTextBar()
    {
        barLabel.setIcon(null);
        textArea.setText(null);
        textArea.setVisible(false);

        // riproduci suono scroll bar
        SoundHandler.playWav(SoundHandler.SCROLL_BAR_PATH, SoundHandler.Mode.SOUND);
    }
}
