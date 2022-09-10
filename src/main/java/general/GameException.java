package general;

/**
 * Classe che rappresenta un'eccezione relativa
 * allo svolgimento del gioco.
 */
public class GameException extends RuntimeException
{
    public GameException(String message)
    {
        super(message);
    }
}
