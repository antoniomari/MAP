package GUI.miniGames;

import GUI.GameScreenPanel;
import GUI.gamestate.GameState;
import general.GameManager;
import general.xml.XmlParser;

import javax.swing.*;
import java.awt.*;

public abstract class MiniGame extends JLayeredPane
{

    private static MiniGame currentTest;
    private static MiniGame lastQuitTest;

    public static void quitCurrentTest()
    {
        System.out.println("PREMUTO");
        currentTest.quit();
        GameState.changeState(GameState.State.PLAYING);
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
    private String victory;
    private String lost;
    private String error;
    static final Integer TEST_LAYER = 1;
    static final Integer RESULT_LAYER = 2;
    final String scenarioOnWinPath = "src/main/resources/scenari/piano MIST/fineTest.xml";

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

            if(checkTestResult())
            {
                GameState.changeState(GameState.State.PLAYING);
                quit();
                GameManager.startScenario(XmlParser.loadScenario(scenarioOnWinPath));
            }
            else
            {
                GameState.changeState(GameState.State.TEST);
            }
        });

        resultPanel.add(okButton, BorderLayout.SOUTH);
    }

    protected abstract boolean checkTestResult();


    protected void showResult(String msg)
    {
        GameState.changeState(GameState.State.TEST_RESULT);
        resultLabel.setText(msg);
        add(resultPanel, RESULT_LAYER);
        resultPanel.setVisible(true);
    }

}
