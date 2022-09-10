package general;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Classe che rappresenta una sequenza di azioni da eseguire.
 */
public class ActionSequence
{
    /** Lista di Runnable, che costituiscono le azioni dell'ActionSequence. */
    private final List<Runnable> actionList;
    /** Indice per ricordarsi dell'ultima azione correntemente eseguita. */
    private int index;
    /** Nome dell'ActionSequence a fini di documentazione. */
    private final String name;

    /**
     * Crea un'ActionSequence, specificandone il nome.
     *
     * @param name nome dell'ActionSequence, utilizzato
     *             solo ai fini di stampa documentativa
     */
    public ActionSequence(String name)
    {
        Objects.requireNonNull(name);

        this.name = name;

        actionList = new ArrayList<>();
        append(GameManager::continueScenario);
        index = 0;
    }


    /**
     * Crea uno scenario vuoto.
     *
     * Da utilizzare nel caso in cui si debba costruire uno scenario
     * che non fa nulla.
     *
     * Nota: non creare uno scenario senza aggiungere azioni:
     */
    public static ActionSequence voidScenario()
    {
        ActionSequence voidScenario = new ActionSequence("Scenario vuoto");
        voidScenario.append(GameManager::continueScenario);

        return voidScenario;
    }

    /**
     * Restituisce il numero di azioni che compongono this
     *
     * @return numero di azioni che compongono this
     */
    public int length()
    {
       return actionList.size();
    }


    @Override
    public String toString()
    {
        return "[== " + name + " ==]";
    }

    /**
     * Aggiungi un'azione in coda.
     *
     * @param action azione da aggiungere
     */
    public void append(Runnable action)
    {
        actionList.add(action);
    }

    /**
     * Esegue la prima azione non ancora eseguita.
     *
     * Il metodo è utilizzato da {@link GameManager}.
     */
    void runAction()
    {
        if(!isConcluded())
            actionList.get(index++).run();
    }

    /**
     * Riporta lo scenario all'inizio.
     *
     * Il metodo è utilizzato da {@link GameManager}.
     */
    void rewind()
    {
        index = 0;
    }

    /**
     * Restituisce {@code true} se lo scenario è concluso.
     *
     * @return {@code true} se lo scenario è concluso, {@code false} altrimenti.
     */
    boolean isConcluded()
    {
        return index == actionList.size();
    }
}
