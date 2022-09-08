Segue un elenco di tutte le possibili azioni e relativa sintassi.

La sintassi di ogni azione è descritta nella documentazione 
che si trova nella javadoc della classe 
general.xml.XmlParser.

- add: utilizzato per aggiungere un GamePiece in una stanza, specifica le coordinate del blocco in basso a sinistra
misurate in blocchi.
- addRoomEffect: utilizzato per aggiungere un'immagine da sovrapporre ala currentRoom e ad i sui componenti. 
Per rendere l'effetto, vengono sovrapposte delle immagini particolari come l'oscuramento della stanza. L'effetto permane
fino al cambio della stanza.
- addToInventory: utilizzato per aggiungere un pickupableItem all'inventario.
- animate: utilizzato per eseguire l'animazione di un GamePiece (metodo "animate").
- animateReverse: utilizzato per eseguire l'animazione di un GamePiece in senso inverso (metodo "animateReverse").
- describeRandomCocktail: utilizzato per creare ed eseguire uno scenario in cui un personaggio descrive una ricetta 
di un cocktail casuale tramite l'utilizzo della classe RecipeRestClient. Gli fa pronunciare prima la categoria del 
cocktail ed in seguito gli ingredienti con le relative dosi.
- executeTest: utilizzato per avviare l'esecuzione di un test (minigame) tra i seguenti possibili:
  - TestMist per il piano MIST
  - LogicQuest e Captcha per il piano ALU
- itemSpeak: utilizzato per simulare l'azione "speak", ma utilizzando un nome arbitrario come nome di colui che parla.
Esempio: utilizzato per far parlare l'ascensore, nonostante non ci sia ne un Item ne un GameCharacter con il nome "ascensorte".
- loadFloor: utilizzato per creare in memoria un nuovo piano. Inizia con il caricare il file XML della stanza confinante
    con l'ascensore eseguendo il suo scenario di inizializzazione proseguendo con il caricamento delle altre seguendo il
    meccanismo descritto nella documentazione alla sezione CREAZIONE DEGLI SCENARI.
- lockEntrance: utilizzato per bloccare o sbloccare l'entrata di una stanza. 
Esempio: stanza-A ha ad est stanza-B, utilizzando il metodo con soggetto "stanza-A" e cardinale "est", blocca l'accesso 
alla stanza B dalla stanza A ma non l'accesso alla stanza A alla stanza B.
- makeSchwartzRobot: utilizzato per richiamare "makePlayerFinalForm" della classe PlayingCharacter.Esso modifica il 
il personaggio giocante iniziale nella sua versione robot.
- move: utilizzato per effettuare lo spostamento di un GamePiece presente in una stanza, alle coordinate specificate. 
Le coordinate specificate possono essere assolute all'interno della stanza o relative alla posizione iniziale dello
stesso GamePiece.
- open: utilizzato per aprire un oggetto che implementi l'interfaccia "Openable".
- playEmoji: utilizzato per riprodurre l'animazione di una emoji a scelta sopra la testa di un NPC.
- playScenarioSound: utilizzato per riprodurre un audio durante uno scenario con l'effetto di proseguire con lo scenario 
solo dopo la fine dell'effetto sonoro.
- removeFromInventory: utilizzato per eliminare un pickupableItem dall'inventario del giocatore.
- removeFromRoom: utilizzato per rimuovere un GamePiece dalla stanza in cui esso è contenuto. Se esso non è contenuto in
alcuna stanza, non ha effetti.
- speak: utilizzato per far pronunciare una frase ad un GameCharacter.
- setSouth, setNorth, setEast, setWest: utilizzati per impostare i collegamenti tra le stanze. La stanza oggetto viene
impostata al punto cardinale specificato, rispetto al punto di vista della stanza soggetto.
- setCanUse: utilizzato per modificare la possibilità di utilizzare un item
  (nota: distinguere Use da UseWith).
- setScenarioOnEnter: utilizzato per impostare uno scenario che deve essere eseguito nel momento in cui il giocatore 
deve entrare in una specifica stanza. Dopo che lo scenario è stato eseguito, rientrando nuovamente nella stanza non 
avverrà nulla, a meno che non venga impostato un nuovo scenario.
- teleport: utilizzato per spostare il giocatore in un altra stanza, la quale deve essere già stata caricata 
nel GameManager.
