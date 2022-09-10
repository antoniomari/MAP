package gui.miniGames;

import gui.GameScreenPanel;
import general.GameManager;
import general.xml.XmlParser;

import javax.swing.*;
import java.awt.*;

public abstract class MiniGame extends JLayeredPane
{

    private static MiniGame currentTest;
    private static MiniGame lastQuitTest;
    private boolean closeOnFail = false;

    private int gameNumber;

    public static void quitCurrentTest()
    {
        System.out.println("PREMUTO");
        currentTest.quit();
        GameManager.changeState(GameManager.GameState.PLAYING);
        lastQuitTest = currentTest;
        currentTest = null;
    }

    static MiniGame getLastQuitTest()
    {
        return lastQuitTest;
    }

    static void setCurrentTest(MiniGame test)
    {
        currentTest = test;
        GameManager.getMainFrame().getGameScreenPanel().add(currentTest, GameScreenPanel.TEXT_BAR_LEVEL);
        currentTest.setVisible(true);
    }

    void quit()
    {
        setVisible(false);
        GameManager.getMainFrame().getGameScreenPanel().remove(this);
    }

    private JPanel resultPanel;
    private JLabel resultLabel;
    protected String victory;
    protected String lost;
    protected String error;
    static final Integer TEST_LAYER = 1;
    static final Integer RESULT_LAYER = 2;
    String scenarioOnWinPath;

    private Component mainPanel;

    private boolean win = false;

    MiniGame(String victory, String lost, String error)
    {
        super();

        this.victory = victory;
        this.lost = lost;
        this.error = error;

        setLayout(null);

        GameScreenPanel gameScreenPanel = GameManager.getMainFrame().getGameScreenPanel();

        Insets screenInsets = gameScreenPanel.getInsets();
        final int width = gameScreenPanel.getWidth() * 3 / 4;
        final int height = gameScreenPanel.getHeight() * 3 / 4;

        final int xOffset = gameScreenPanel.getWidth() / 8;
        final int yOffset =  gameScreenPanel.getHeight() / 8;

        setPreferredSize(new Dimension(width, height));
        setBounds(screenInsets.left + xOffset, screenInsets.top + yOffset,
                width, height);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        resultPanel.setPreferredSize(new Dimension(width/2, height/2));
        //resultPanel.setLocation((int) (getInsets().left + getPreferredSize().getWidth() / 4), (int) (getInsets().top + getPreferredSize().getHeight() / 4));
        resultPanel.setBounds(width/4, height/4, width/2, height/2);
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultPanel.add(resultLabel, BorderLayout.CENTER);

        resultPanel.setVisible(false);

        JButton okButton = new JButton("Ok");
        okButton.setFocusable(false);

        okButton.addActionListener((e) ->
        {
            resultPanel.setVisible(false);
            remove(resultPanel);
            mainPanel.setVisible(true);

            if(win)
            {
                GameManager.changeState(GameManager.GameState.PLAYING);
                quit();
                GameManager.startScenario(XmlParser.loadScenario(scenarioOnWinPath));
            }
            else
            {
                GameManager.changeState(GameManager.GameState.TEST);

                if(closeOnFail)
                {
                    quitCurrentTest();
                }
            }
        });

        resultPanel.add(okButton, BorderLayout.SOUTH);
    }

    protected void setMainPanel(Component mainPanel)
    {
        this.mainPanel = mainPanel;
        add(mainPanel, TEST_LAYER);
    }

    protected void showResult(String msg)
    {
        mainPanel.setVisible(false);
        GameManager.changeState(GameManager.GameState.TEST_RESULT);
        resultLabel.setText(msg);
        add(resultPanel, RESULT_LAYER);

        if(msg.equals(victory))
            win = true;

        resultPanel.setVisible(true);
    }

    protected void setCloseOnFail(boolean b)
    {
        this.closeOnFail = b;
    }

}
