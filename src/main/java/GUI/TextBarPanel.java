package GUI;

import graphics.SpriteManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TextBarPanel extends JLayeredPane
{
    // Path immagini e json //
    private final static String TEXT_BAR_PATH = "/img/barra di testo/text bar.png";

    // Immagini png (da caricare) //
    private final static BufferedImage TEXT_BAR_IMAGE;

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
         textArea.setFont(new Font("Dialog", Font.BOLD, 32 ));
         textArea.setRows(2);
         int horizontalBorder = (int) (20 * scalingFactor);
         int verticalBorder = (int) (15 * scalingFactor);
         textArea.setBounds(horizontalBorder, verticalBorder,
                              barIcon.getIconWidth() - 2 * horizontalBorder,
                              barIcon.getIconHeight() - 2 * verticalBorder);

         add(textArea, TEXT_LEVEL);
    }

    public void showTextBar(String text)
    {
        barLabel.setIcon(barIcon);
        String text1 = "una bella giornata passata in compagnia a fare coding";
        textArea.setText(text + "\n" + text1);
        textArea.setVisible(true);
    }

    public void hideTextBar()
    {
        barLabel.setIcon(null);
        textArea.setText(null);
        textArea.setVisible(false);
    }
}
