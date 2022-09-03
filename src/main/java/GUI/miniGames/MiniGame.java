package GUI.miniGames;

import GUI.GameScreenPanel;
import GUI.gamestate.GameState;
import general.GameManager;

import javax.swing.*;

public class MiniGame extends JLayeredPane
{

    private static MiniGame currentTest;
    private static MiniGame lastQuitTest;

    public static void quitCurrentTest()
    {
        currentTest.setVisible(false);
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
    }
    /*
    protected MiniGame() extends JPanel
    {

    }

    public void executeMinigame()
    {
        MiniGame game = new MiniGame();
    }

    public static void main()
    {
        JFrame mainFRame = new JFrame();
        MiniGame game = new MiniGame();


        SwingUtilities.invokeLater();
    }

     */

}
