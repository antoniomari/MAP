package entity.items;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface Openable
{
    /** Esegue l'interazione "open". */
    void open();

    /** Esegue l'interazione "close". */
    void close();

    /** Restituisce i frame per la costruzione dell'animazione associata all'interazione "open". */
    List<Image> getOpenFrames();

    /** Restituisce i frame per la costruzione dell'animazione associata all'interazione "close". */
    List<Image> getCloseFrames();

    /**
     * Restituisce {@code true} se this è aperto.
     *
     * @return {@code true} se this è aperto, {@code false} altrimenti
     */
    boolean isOpen();


    /**
     * Carica il dizionario contente le coppie stato->scenario,
     * contenente i path degli scenari da eseguire all'interazione
     * "open" a seconda dello stato in cui si trova this.
     *
     * @param openScenarioMap dizionario contenente le coppie stato->scenario
     */
    void loadOpenScenarios(Map<String, String> openScenarioMap);
}
