# Salvataggi
Il db viene utilizzato per i salvataggi del giocatore. In un momento prestabilito (da decidere),
il gioco viene salvato automaticamente. 

Per poter realizzare un salvataggio è necessario tenere traccia di tutte le informazioni che costituiscono
lo stato del gioco. Queste sono:

1. Dati sui piani
    - La mappa è statica, per cui non c'è bisogno di salvare i collegamenti tra le stanze (eccezione: ascensore)
2. Dati sulle stanze
    - _scenarioOnEnter_
    - 
3. Dati sugli oggetti e sui personaggi