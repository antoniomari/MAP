# 2. Struttura del software e scelte implementative.
## 2.1 Scenari
Uno scenario è una sequenza di azioni programmabili che avvengono automaticamente all’interno del gioco. 
L’esecuzione degli scenari è gestita dal GameManager e possono essere creati all’interno del codice del gioco
oppure essere codificati all’interno di un file XML per poter essere caricati tramite la classe XmlParser. 
La classe utilizzata per rappresentare uno scenario è General.ActionSequence.
Ogni oggetto di ActionSequence corrisponde concettualmente ad una sequenza di runnable, affinché sia generale e 
ogni runnable contenuta all’interno di essa dovrà contenere una chiamata al metodo GameManage::ContinueScenario. 
Questo metodo permette di notificare al GameManager di notificare quando deve proseguire con l’esecuzione del runnable 
successivo. Questo è necessario in quanto l’ok per la continuazione dello scenario può non essere inviato direttamente 
dal runnable corrente, ma da un thread esterno, come ad esempio un input dato dal giocatore.
Il GameManager utilizza una pila. Quando qualcuno invia una chiamata al metodo start.Scenario, allora la action 
sequence passata come parametro viene pushata nella pila. Se lo scenario era già stato precedentemente eseguito e 
viene riusato, allora esso ripartirà dall’inizio. In seguito viene eseguita l’azione corrente dello scenario che si 
trova al top della pila. Quando viene chiamato il metodo continueScenario, possono accadere due cose:
1. o la pila contiene come top uno scenario non concluso, in tal caso si esegue la sua prossima azione
2. oppure il top è concluso, quindi lo si rimuove dalla pila e si controlla se il nuovo top non sia ancora concluso, 
in tal caso si esegue la sua prossima azione. Se invece era anch’esso concluso, si controlla il successivo, e così via, 
fino allo svuotamento della pila.

### 2.1.1 Creazione degli scenari: Parsing degli scenari da file xml
Il metodo loadScenario della classe XmlLoader costruisce una action sequence risultante dal parsing di un file xml. 
Il root tag del file xml deve essere <scenario>, al suo interno vi sarà un numero arbitrario di nodi xml con il tag 
<azione>. Ogni nodo <azione> contiene al suo interno almeno un nodo <method>, inoltre può contenere nodi con tag 
particolari seguendo la sintassi prevista dalla primitiva ((dare nome a primitiva)) specificata dal contenuto 
dell’elemento <method>.
Ogni xml di una stanza deve contenere all’interno dell’elemento stanza, obbligatoriamente, un elemento scenario: 
lo scenario di inizializzazione della stanza. Può essere eventualmente vuoto e viene utilizzato in genere per 
specificare i collegamenti tra le stanze o per aggiungere un game peace alla stanza stessa.
Consultare la documentazione delle “primitiva” per la sintassi.
Quando una stanza viene caricata per la prima volta nella partita (non salvataggio caricato da database), gli scenari 
di inizializzazione vengono eseguiti per intero. Mentre quando le stanze vengono caricate da database allora del suo 
scenario di inizializzazione vengono ignorate tutte le azioni tranne i metodi “setNorth”, “setSouth”, “setWest” e “setEast”.
L’inizializzazione delle stanze procede come una visita in profondità di un grafo, infatti appena viene eseguita 
un’azione qualsiasi che riporta uno dei quattro metodi elencati sopra, viene caricata la stanza oggetto e viene eseguito 
il relativo scenario di inizializzazione, a seguito dello scenario della prima. 

## 2.2 Salvataggi e Database
Il database GAME viene utilizzato per memorizzare in maniera persistente tutte le informazioni al momento del 
salvataggio, necessarie successivamente a ricostruire lo stato del gioco.

### 2.2.1 Tabelle
Nelle tabelle vengono memorizzate esclusivamente le informazioni che possono mutare durante il gioco e dunque non 
presenti all’interno dei file XML utili al caricamento.

Nella tabella ROOM vengono salvati:
- Nome della stanza
- Path del file XML che la descrive
- Path dello scenario onEnter che risulta impostato al momento del salvataggio

Nella tabella LOCKENTRANCE vengono salvate le coppie <room, punto cardinale> tali per cui l’entrata nella stanza da 
tale punto cardinale risulta bloccata.
Nota: le entrate non bloccate, essendo la maggior parte ed essendo impostate di default, non vengono memorizzate.

Nella tabella ITEM vengono salvati:
- Name
- State
- CanUse 

State e CanUse sono informazioni che possono variare durante il gioco.

Nella tabella GAMECHARACTER vengono salvati:
- Name
- State 

Nelle tabelle ITEMLOCATION e CHARACTERLOCATION vengono memorizzate le presenze e posizioni di, rispettivamente, 
Item e gameCharacter, all’interno delle stanze. Queste tabelle sono uguali, ma separate in quanto impossibile creare 
un vincolo di integrità referenziale verso più tabelle contemporaneamente.

Nella tabella INVENTORY vengono salvati i nomi dei PickupableItem presenti all’interno dell'inventario del giocatore 
al momento del salvataggio.

Utilizzo all’interno del gioco:

All’avvio del gioco, se il database non è presente (esempio: al primo avvio), esso viene creato.
Se il giocatore preme il tasto “continua” vengono, in ordine, caricate prima le stanze, ripristinando i loro 
collegamenti tramite i rispettivi file XML e bloccando le entrate specificate nella tabella LOCKENTRANCE, in seguito 
vengono caricati i GamePiece tramite i file oggetti.xml e personaggi.xml. Essi vengono quindi inseriti nelle stanze ed 
infine viene caricato l’inventario.
Se il giocatore preme il tasto “salva”, viene prima eliminato il contenuto del database, sovrascrivendo il precedente 
salvataggio, poi vengono salvate, in ordine, le informazioni su Room, GamePiece e l’inventario.

## 2.3 API REST
È stato utilizzato il servizio api fornito dal sito https://www.themealdb.com, il quale, in seguito ad una chiamata, 
restituisce un JSON che contiene le informazioni relative ad una ricetta casuale.
La classe RecipeRestClient è basata sull’utilizzo del framework fornito dalla libreria Jersey per effettuare la 
richiesta al server ed ottenere la relativa risposta.
Dal JSON contenente la risposta vengono prelevate le informazioni da utilizzare all’interno del gioco, quali la 
categoria della ricetta e gli ingredienti con le relative dosi.
In caso di insuccesso della chiamata, vengono utilizzati dei valori di default sostitutivi.


## 2.4 Eventi e Architettura del sistema
Il GameManager è una classe adibita alla gestione di strutture dati fondamentali per far funzionare il gioco. 
Esso contiene variabili e costanti utilizzati da diverse classi per diversi scopi.
Ad esempio, blockSize è il numero di pixel che costituisce il lato di un blocco, oppure il riferimento al mainFrame 
del gioco.
Contiene riferimenti a tutti i GamePiece e Room caricati in memoria e gestisce lo stack degli scenari. 
Alla costruzione di un nuovo GamePiece o di una nuova Room, essi vengono automaticamente registrati rendendo possibile 
il loro utilizzo da parte di tutti i metodi di ogni package riferendosi per nome. È dunque importante che non vi siano 
più GamePiece o Room con lo stesso nome.

## 2.4.1 Sezione GAMESTATE (forse da unire sopra)
Un GameState è uno stato in cui si può trovare il gioco e determina quali azioni sono possibili e quali no. 
Ad esempio nello stato “palying” il giocatore può liberamente muoversi all’interno delle stanze ed interagire con 
oggetti e personaggi, mentre nello stato “textBar” non sarà possibile effettuare nessuna di queste interazioni, ma il 
giocatore potrà unicamente premere il tasto Barra Spaziatrice per chiudere la text-bar (mandando avanti il gioco, 
che potrebbe tornare allo stato playing). Per tale motivo abbiamo implementato i listener di tastiera e mouse 
(rispettivamente GameKeyListener e GameMouseListener) in modo tale che questi siano attivi e funzionino solo in un 
determinato gameState, denominato targetState.

## 2.5 Listener
Alcuni listener vengono creati staticamente dalla classe GameState: sono listener che operano su tutto il frame di 
gioco e corrispondono a comandi ben definiti, ad esempio le frecce direzionali utilizzate per cambiare stanza nello 
stato “playing” oppure la barra spaziatrice per chiudere la text-bar.
Esistono inoltre listener creati dinamicamente sul gameScreen che corrispondono a diversi elementi nel gioco, ve n'è 
almeno uno per ogni gamePiece (in quanto deve essere almeno possibile premere il tasto destro sul personaggio e 
mostrare il menu con le possibili interazioni con esso).
La creazione di queste liste avviene nel GameScreenPanel.
Per il cambio di stato viene mantenuta una variabile statica che identifichi lo stato corrente del gioco. 
Il metodo changeState permette di cambiare il suo valore e ha la key word “synchronize”, così come il metodo getState, 
in quanto lettura e scrittura dello stato possono avvenire su thread diversi (ad esempio, il thread di un’animazione di 
movimento all’inizio imposta il gameState a “moving”).

Gli elementi di gioco hanno due rappresentazioni:

- la rappresentazione di dominio in cui possono essere pensati come entità e ciò riguarda il package entity. 
I metodi e le interazioni tra le classi di questo package non fanno in alcun modo riferimento all’interfaccia che viene 
utilizzata per giocare. Sono dunque indipendenti dall’interfaccia ed esprimono unicamente i concetti e gli oggetti 
“fisici” del gioco. Tuttavia bisogna trovare un modo per far sì che essi abbiano una rappresentazione nella User 
Interface e bisogna aver stabilito precise regole di interazione in modo che l’input dell’utente tramite UI abbia 
effetti sulle entity, mentre aggiornamenti sulle entity siano correttamente propagati nelle loro rappresentazioni
relativamente alla user interface. Per questo coordinamento è stato pensato un sistema ad eventi, creando una classe 
astratta generica GameEvent che viene specializzata tramite le sue sottoclassi in modo tale che gli oggetti entity, 
quando aggiornati, generino eventi specifici e li inviino alla classe EventHandler, la quale si occupa di distinguere i 
vari tipi di eventi e di delegare a classi executor il compito di aggiornare in modo coerente con l’accaduto la User 
Interface.
Tali executors sono gli unici ad avere riferimento al mainFrame in modo tale da essere l’unico collegamento 
entity -> GUI.

- la rappresentazione di input (GUI -> entity) è possibile grazie alla classe PopMenuManager. 
Tramite il metodo addPieceOnScreen della classe GameScreenPanel, quando viene aggiunta la label con lo sprite di un 
GamePiece, viene creato un listener per il tasto destro che prevede la chiamata al metodo showMenu di PopMenuManager, 
passando sia il GamePiece che la label corrispondente. Il PopMenuManager crea un menu contestuale aggiungendo vari tasti 
corrispondenti alle varie interazioni possibili, in base alla classe effettiva del GamePiece passato. Infatti crea 
staticamente delle Action per ogni interazione possibile con qualsiasi GamePiece, in modo tale che cliccando sul tasto 
corrispondente venga eseguito il metodo relativo all’interazione per GamePiece sul quale si è cliccato.
Ad esempio, se il GamePiece è un’istanza di Item verrà abilitata una useAction in modo tale che venga chiamato il 
metodo Use dell’oggetto della classe Item.

## 2.6 Entity
Le classi del package entity rappresentano i principali elementi del dominio di gioco, i quali non dipendono
dall’interfaccia grafica.
In realtà questo package comprende ulteriori tre packages (entity.characters, entity.items, entity.rooms) i quali 
esistono solo per un fattore di ordine e pulizia nella struttura del codice.
Si riporta di seguito il diagramma delle classi di questi packages, in quanto si ritiene un aspetto cardine per la 
comprensione del dominio di gioco.

---- diagramma delle classi 


### 2.6.1 GamePiece
Un oggetto della classe astratta *GamePiece* rappresenta un qualsiasi elemento fisico del gioco che ha uno sprite e può 
essere presente all’interno di una stanza, in pratica un oggetto o un personaggio. La classe è astratta in quanto ogni 
oggetto deve necessariamente estendere una delle sottoclassi dirette (`Item` e `GameCharacter`).
- Ogni GamePiece ha un nome univoco ed uno stato, ovvero una stringa utilizzata per 
far si che un GamePiece abbia comportamenti diversi a seconda del suo valore.
- Un GamePiece può essere creato fornendogli un'immagine che costituirà il suo sprite principale, oppure uno sprite 
sheet descritto da un relativo file JSON. In questo ultimo caso, lo sprite principale viene ricavato cercando nel json 
l'oggetto il cui nome è pari a quello del GamePiece.
- Un GamePiece ha una posizione nella stanza in cui esso è contenuto: questa posizione è misurata in blocchi 
(vedi `BlockPosition` di `Room`) e corrisponde sempre alle coordinate del blocco in basso a sinistra del GamePiece
(considerando il fatto che il blocco in alto a sinistra ha coordinate (0,0).
- Presenta le seguenti liste di immagini: 
  - Immagini `leftMovingFrames`, utilizzate per le animazioni di movimento verso sinistra
  - Immagini `rightMovingFrames`, utilizzate per le animazioni di movimento verso destra
  - Immagini `animateFrames`, utilizzate per le animazioni personalizzate (metodi `animate` e `animateReverse`)
  - Immagini `perpetualAnimationFrames`, utilizzate per l'animazioni perpetua personalizzata.

Le prime due liste di default conterranno solamente lo sprite principale. Per questo motivo, quando il GamePiace si 
muove sullo schermo lo sprite non cambia.
Per la terza lista l'inizializzazione avviene chiamando il metodo `initAnimateFrames`, fornendo i path dello sprite-sheet 
del JSON. I frame vengono cercati tramite oggetti JSON con chiavi del tipo "animate1", "animate2"...
Se questi non vengono trovati allora cerca gli oggetti json le cui chiavi sono "\[name]animate1", "\[name]animate2", ...
Per la quarta lista l'inizializzazione avviene chiamando il metodo `initPerpetualAnimationFrames`, fornendo i path 
dello sprite-sheet del JSON. I frame vengono cercati tramite oggetti JSON con chiavi numerate ed incrementali.

### 2.6.2 Item
Item è un GamePiece il cui sprite viene caricato dallo sprite-sheet `oggetti.png` e relativo JSON e presenta:
- Una descrizione
- Un'interazione “usa” specializzata: il nome di tale azione può essere personalizzato, così come il suo effetto.
Ad ogni diverso stato in cui si trova l'item corrisponderà un diverso scenario all'esecuzione dell'interazione "usa":
tali informazioni sono contenute nel file "oggetti.xml".

Implementando l’interfaccia Osservable, un Item avrà associato l’interazione "osserva", il cui effetto è mostrare la 
descrizione dell’oggetto.

#### 2.6.3 PickupableItem
Un `PickupableItem` è un item che può essere raccolto e inserito nell’inventario. Quando è nell'inventario offre
l'interazione "useWith": può cioè essere utilizzato assieme ad un GamePiece presente sullo schermo. Questa interazione 
si esegue selezionando il PickupableItem e successivamente premendo il tasto sinistro sul GamePiece con cui si vuole interagire.
Secondo la sintassi spiegata in `oggetti.xml` è possibile impostare il GamePiece target dell’azione useWith e lo stato 
in cui deve essere il target affinché lo scenario corrispondente all’interazione venga eseguito.
Viene specificato inoltre lo stato al quale deve essere impostato il target al termine dell'interazione. 
È anche possibile specificare se il PickupableItem rimarrà nell'inventario in seguito all'utilizzo oppure dovrà essere 
rimosso.

#### 2.6.4 DoorLike
Un oggetto della classe `DoorLike` è un item le cui funzionalità sono assimilabili a quelle di una porta (infatti implementa 
l’interfaccia Openable).
- È possibile impostare uno scenario da eseguire all'interazione "open" che verrà eseguito se l'oggetto si trova in
particolare stato. Vi è uno stato riservato, ovvero "canOpen", durante il quale lo scenario eseguito all'interazione
"open" sarà l’effettiva apertura dell'oggetto DoorLike. 
- Non è invece possibile impostare uno scenario personalizzato per l’interazione Close, che risulterà sempre nella 
chiusura dell'oggetto doorLike.

La creazione degli oggetti della classe DoorLike prevede il recupero e l'impostazione dello sprite tramite i file 
`porte.png` e nel file JSON corrispondente, all’interno del quale viene cercato l'oggetto JSON la cui chiave è pari al 
nome dell'oggetto DoorLike in fase di costruzione.

Ogni oggetto DoorLike presenta due liste d'immagini:
- Immagini `openFrames`, utilizzate per l'animazione di apertura dell'oggetto DoorLike
- Immagini `closeFrames`, utilizzate per l'animazione di chiusura dell'oggetto DoorLike


Queste liste sono inizializzate durante la creazione dell'oggetto DoorLike, ricercando nel file JSON gli oggetti JSON 
con chiavi del tipo "\[name]Open1", "\[name]Open2", ..., e un oggetto JSON con chiave "\[name]Closed".


--------------------------------------------
Per ogni oggetto della classe DoorLike vi sono le interazioni "Open" e "Close", che sono unificate nel
menu contestuale.
--------------------------------------------



### 2.6.3 GameCharacter
I `GameCharacter` sono GamePiece che rappresentano i personaggi all’interno del gioco.
Ogni GameCharacter che viene creato utilizzando uno sprite-sheet ed un file JSON associato inizializza i frame di 
movimento `leftMovingFrames` e `rightMovingFrames` nel seguente modo (si riporta un esempio per i primi):
- Cerca oggetti json "leftMoving1", "leftMoving2", ..., dove name è il nome del GameCharacter;
- Se questi non sono trovati cerca oggetti json "\[name]leftMoving1", "\[name]leftMoving2", ...;
- infine aggiunge come primo frame lo sprite principale del GameCharacter.

Il file JSON deve anche contenere la voce “speaking” per indicare lo sprite che contribuisce a realizzare 
l’animazione del personaggio che parla. Inoltre un GameCharacter prevede anche un frameSpeaking che viene inizializzato 
come segue:
- Cerca nel json relativo allo sprite-sheet l'oggetto "speaking";
- Se questo non è presente cerca l'oggetto "\[name]speaking", dove \[name] è il nome del GameCharacter; 
- se neanche questo è presente allora esegue l'inizializzazione di default.


### 2.6.4 NPC
Un NPC è un non-playable-character, ovvero un GameCharacter che non può essere utilizzato dal giocatore. 
Esso offre l’interazione “parla”, al verificarsi della quale verrà stampata sulla text-bar una frase oppure scenario 
impostati nel file xml (a seconda dello stato in cui si trova l’npc).

### 2.6.5 PlayingCharacter
`PlayingCharacter` è una classe i cui oggetti rappresentano il personaggio giocante ed implementa il pattern Singleton, 
infatti i dati sul player non sono presenti all’interno del file xml, ma il giocatore viene creato staticamente 
all’inizio del gioco e non possono essere creati altri PlayingCharacter se non per un’eccezione, ovvero il metodo 
makePlayerFinalForm il quale sostituisce il personaggio giocante con uno dallo stesso nome ma non uno sprite diverso 
(necessario per la trama del gioco).

Un oggetto PlayingCharacter possiede un inventario, ovvero una struttura dati (attualmente una lista) che contiene un 
certo numero di PikupableItem. Notare che tale inventario ha una diretta rappresentazione grafica nella classe 
InventoryPanel e l’inventario del PlayingCharacter quando viene aggiornato, notifica tramite InventoryEvent 
l’inventory panel il quale a sua volta modifica la visualizzazione delle icone degli oggetti.

### 2.6.6 Rooms
Un oggetto `Room` è una stanza del gioco ed è identificata da un nome (invisibile al giocatore), può possedere fino a 
quattro collegamenti, uno per ogni punto cardinale, con altre stanze. Ad ogni stanza è associato un file XML che 
contiene tutte le informazioni statiche, come ad esempio il path dell’immagine di background o il path della musica 
di sottofondo.

Ogni room possiede un proprio pavimento, ovvero un oggetto di tipo RoomFloor e una posizione di default alla quale 
posizionare il personaggio giocante.

Può essere impostato uno scenario da avviare quando il giocatore entra nella stanza e le entrate possono essere 
bloccate o sbloccate. Ogni stanza inoltre mantiene i riferimenti a tutti i pezzi presenti e relative posizioni.

Ad ogni room è associato un file JSON con la seguente struttura:
- Deve avere width e height, un json array chiamato “pavimento” che contiene oggetti json con dentro “left”, “top”, 
“width”, “height”, che descrivono un rettangolo specificandone l’angolo in alto a sinistra e le dimensioni.
- Ostacoli, un json array che contiene oggetti descritti come sopra.
- Un oggetto DefaultPosition che contiene X e Y
- Per ogni entrata nella stanza, un oggetto con nome punto_cardinaleArrow (es. northArrow) contenente a sua volta X e Y.

#### 2.6.6 BlockPosition
Tutte le posizioni, le coordinate e le dimensioni relative ai GamePiece e alle Room sono espresse in blocchi: un blocco 
corrisponde ad un tile nell’immagine della stanza e le coordinate in una qualsiasi stanza si contano considerando 
l’angolo in alto a sinistra della stanza, avente coordinate spaziali (0,0) ed incrementando le ascisse da sinistra 
verso destra e le ordinate dall’alto verso il basso.

Notare che, dal momento in cui tali coordinate sono logiche e non fisiche, non hanno alcuna dipendenza dallo schermo 
(quindi rendono i dati portabili) e risulta molto semplice lavorarci.
Sarà compito della classe GameScreenManager la conversione da block position ad abs position 
(vedi Sezione GameScreenManager).


### 2.7 RoomFloor
Un oggetto di RoomFloor rappresenta il pavimento di una stanza, la cui inizializzazione è possibile tramite i dati 
contenuti nel json della stanza alle voci “pavimento” e “ostacoli”. Il pavimento è una forma data dalla composizione 
dei rettangoli descritti nel json del pavimento, all’interno della quale il personaggio giocante può muoversi. Ci sono 
però ulteriori rettangoli dati dalla voce ostacoli che rappresentano aree del pavimento su cui il giocatore non può 
muoversi. Ciononostante attualmente l’utilizzo del RoomFloor è parziale in quanto non è presente un algoritmo di 
movimento che ricalcoli il percorso da un punto di partenza ad uno di arrivo tenendo conto degli ostacoli ed 
aggirandoli. Attualmente viene utilizzato il metodo getNearestPlacement di RoomFloor per far sì che anche se dovesse 
essere impostato il movimento del giocatore in un punto in cui lo sprite non entra perfettamente allora questo viene 
comunque posizionato, occupando la posizione più vicina che è possibile occupare.


### 2.8 Suoni
Per i suoni è stata utilizzata la libreria javax.sound.sampled, la quale permette di riprodurre file in formato wav. 

Si distinguono tre tipologie di audio nel gioco, che corrispondono a modalità di riproduzione accettate dalla classe 
SoundHandler:
- music, modalità utilizzata per riprodurre le musiche di sottofondo delle stanze le quali sono messe a loop e vengono 
stoppate solamente al passaggio in una stanza in cui la musica impostata è diversa.
- sound, modalità utilizzata per riprodurre suoni di gioco generati da input del giocatore, attualmente i suoni di questo
tipo vengono utilizzati: alla chiusura della text-bar, premendo la barra spaziatrice, quando si passa il mouse su un 
tasto del menu, quando viene aggiunto un oggetto all’inventario o quando viene eseguita l’animazione di una emoji. 
Tali suoni hanno dei path memorizzati nel SoundHandler, per cui le relative clip sono registrate staticamente, 
eliminando l’esigenza di  aprire i file ogni qualvolta devono essere riprodotti.
- scenario sound, modalità dedicata all’azione “playScenarioSound” degli scenari, la quale fa sì che ogni volta che 
venga aperto il file specificato dall’azione, viene utilizzata la clip scenarioSound inizializzata staticamente e 
alla quale è aggiunto un lineListener che all’evento di stop (quindi quando termina l’audio riprodotto dall’azione 
dello scenario) invoca il metodo GameManager.contiueScenario.

Nota: Il listener è obbligatorio in quanto non è possibile avere il controllo sul thread dell’audio in riproduzione e 
quindi non avremmo altro modo per eseguire tale chiamata al momento in cui termina la riproduzione.

### 2.9 Animazioni
Il package animazioni contiene la classe astratta animation e le sue implementazioni:
- stillAnimation, rappresenta un’animazione in cui cambia lo sprite di un GamePiece o di una qualsiasi JLabel, ma non 
la sua posizione.
- movingAnimation, rappresenta un’animazione in cui cambia la posizione di una JLabel e può cambiare contestualmente 
anche la sua icona.
- perpetualAnimation, sottoclasse di stillAnimation e rappresenta un’animazione in cui non cambia la posizione di una 
JLabel ma la stessa animazione viene ripetuta in loop all’infinito finché non viene interrotta da un qualche evento 
esterno.

Ogni animazione presenta un thread dedicato su cui viene eseguita, permettendo quindi l’esecuzione di più animazioni 
contemporaneamente e l’esecuzione di animazioni in modo indipendente dall’interazione del giocatore con il gioco.

#### 2.9.1 Animation
Ogni animazione interviene su una JLabel presente sullo schermo e consiste nell’alternarsi di una certo numero di frames 
(immagini). Possono essere impostati i millisecondi da attendere alla fine dell’esecuzione dell’animazione e 
successivamente alla creazione viene eseguita chiamando il metodo start (solo in quel momento viene chiamato il thread 
dedicato).

L’esecuzione dell’animazione (ovvero il metodo run dell’AnimationThread) segue il design pattern del template metod. 
Infatti questa classe astratta implementa il run come una chiamata al metodo astratto execute seguito da thread.sleep 
per il numero dei millisecondi impostato ed infine chiama il metodo terminate anch’esso astratto. Le sottoclassi 
implementeranno tali metodi astratti in modo tale da specializzare il modo in cui viene aggiornata la JLabel da animare 
e le azioni da eseguire al termine.

#### 2.9.2 StillAnimation
Una stillAnimation è l’animazione di una JLabel in cui questa cambia icona ma non posizione. È possibile impostare il 
delay in millisecondi tra la visualizzazione di un frame ed il successivo e se tale delay vi è anche prima dell’inizio 
dell’animazione.

L’esecuzione di tale animazione prevede innanzitutto un cambio di GameState nello stato moving e in seguito 
un’iterazione su tutti i frame in cui si aspetta il delay impostato e si aggiorna l’icona della Label con il frame 
successivo. È possibile impostare una runnable da eseguire al termine dell’animazione e alla fine il thread dedicato 
all’animazione manda avanti lo scenario attraverso la chiamata a GameManager.continueScenario.

La classe mette inoltre a disposizione una static factory per poter creare animazioni personalizzate tramite sprite 
sheet e json in cui vengono fornite le informazioni sulla posizione dei vari frame (nel json oggetti con chiave 
“nomeAnimazione_Numero”, es. Esplosione1, Esplosione2, …).

Utilizzi:
- Animazione alla pressione dei tasti dell’inventario
- Creazione di effetti animati personalizzati, relativi a GamePiece, che utilizzano JLabel sovrapposte a quelle dei 
- GamePiece (GameScreenPanel.StackAnimation)
- Nelle interazioni con gli item che includono un’animazione (es. apertura porte)
- Al metodo animate ed animateReverse dei GamePiece

#### 2.9.3 MovingAnimation
Una movingAnimation è un’animazione in cui una JLabel cambia posizione, cambiando eventualmente icona. Per la JLabel è 
possibile impostare posizione iniziale, posizione finale, i frame da alternare in loop durante tutta l’esecuzione 
dell’animazione e la velocità alla quale questa deve essere eseguita.
Sulla base dei parametri ricevuti calcola una sequenza di coordinate equidistanti in cui la JLabel viene posizionata 
ad ogni iterazione nell’esecuzione. Tali posizionamenti procedono partendo dalla posizione iniziale ed avvicinandosi 
verso la posizione finale.

Nota: il calcolo delle coordinate intermedie viene fatto tramite le abs position e il numero di queste coordinate 
intermedie dipende dalla variabile statica FPS della classe (attualmente impostato a 60) e dalla velocità impostata 
per l'animazione. Il caso di lag, considerare di abbassare tale variabile. Anche il delay tra iterazioni successive 
nell’esecuzione dell’animazione viene calcolato sulla base di questi dati.

Ogni 10 cambi di coordinate intermedie viene aggiornata l’icona della JLabel con il frame successivo tra quelli 
impostati o con il primo, se l’attuale è l’ultimo. Al termine dell’esecuzione dell’animazione viene reimpostato il 
frame iniziale della label e viene continuato lo scenario tramite la chiamata al metodo continueScenario della classe 
GameManager.

Nota: è richiesto che la lista di frames dati all’animazione contenga in posizione zero il frame iniziale della label 
e dalla posizione uno in poi tutti i frame che devono alternarsi.

Utilizzi:
- Movimenti dei GamePiece sullo schermo

#### 2.9.4 PerpetualAnimation
Una PerpetualAnimation è un’animazione perpetua di una JLabel. l’animazione data dal susseguirsi dei frames impostati 
viene ripetuta finché un evento esterno non la ferma. È possibile settare le stesse impostazioni della superclasse 
(StillAnimation), nonché l’icona da impostare alla label animata al momento in cui l’animazione viene interrotta.
Vi è uno static factory per creare PerpetualAnimation che si interrompono automaticamente non appena cambia lo stato 
di gioco (ovvero si imposta uno stato di gioco in cui l’animazione è attiva). Cambiato lo stato di gioco, se si ritorna 
nello stesso stato, l’animazione si è comunque conclusa e non ripartirà. In modo simile rispetto alla superclasse, 
mette a disposizione delle factory per creare animazioni personalizzate fornendo spritesheet e json seguendo la stessa 
sintassi.

Quindi l’esecuzione dell’animazione non prevede il cambio di stato, ma prevede un ciclo while-true che avvolge 
un’iterazione simile a quella che coinvolge stillAnimation, con la differenza che una chiamata esterna al metodo “stop” 
farà sì che il thread esca dal ciclo.

Alla terminazione verrà eseguito il runnable impostato, ma non viene mandato avanti lo scenario.

Utilizzi:
- Quando viene aggiunto un GamePiece sullo schermo, se questo possiede un’animazione perpetua allora questa viene 
creata ed eseguita.
- Per le animazioni di dialogo dei personaggi, interrotte quando il dialogo viene mandato avanti dal giocatore
- Nel titolo della schermata iniziale del gioco
- Nella creazione di animazioni personalizzate sui GamePiece.













## 2.... XML
I dati su tutti gli oggetti esistenti nel gioco sono presenti nel file oggetti.xml, questo file ha come root-tag 
oggetti all’interno del quale ci sono nodi xml con tag oggetto e attributo nome = nome dell’oggetto.
I dati degli oggetti sono descritti con i tag:
- \<classe>: Classe effettiva dell’oggetto
- \<descrizione>: Descrizione
- \<canUse>: true se l’eventuale azione dell’oggetto è abilitata, false altrimenti
- \<onUse>: contiene a sua volta i tag
  - \<actionName>: nome dell’azione personalizzata
  - \<useScenario state = “nameState”>: path dello scenario da eseguire quando l’oggetto viene usato (si clicca il 
  tasto corrispondente all’azione nel menu contestuale, o quando l’oggetto è nell’inventario, è selezionato e si clicca 
  sul giocatore).

Nota: ci possono essere più tag <useScenario> per associare comportamenti diversi a seconda dello stato.

- \<animazionePerpetuaPng>: path dello spritesheet dell’animazione perpetua dell’oggetto
- \<animazionePerpetuaJson>: path del file json relativo all’animazione perpetua dell’oggetto
- \<onUseWith>: contiene a sua volta i tag
  - \<keep>: se l’oggetto deve essere mantenuto nell’inventario dopo l’interazione useWith
  - \<target>: GamePiece target dell’interazione useWith
  - \<targetInitState>: è lo stato in cui deve trovarsi il target affinché l’interazione avvenga, quindi lo scenario venga eseguito
  - \<targetFinalState>: lo stato in cui viene impostato il target alla fine dello scenario
  - \<scenario>: path dello scenario associato all’interazione
- \<onOpen>: che a sua volta contiene i tag:
  - \<Scenario state = “nameState”>: path dello scenario da eseguire quando si chiama il metodo open

## XML dei personaggi
Questo file ha come root-tag “personaggi”, all’interno del quale ci sono nodi xml con tag “personaggio” e attributo 
“nome=nome personaggio”. I tag possibili sono:
- \<spriteSheet>: path dello sprite sheet o dello sprite del personaggio
- \<json>: (opzionale) path del json corrispondente allo spritesheet
- \<speakScenarios>: che a sua volta contiene i tag
  - \<sentence state = “nameState”>: frase da pronunciare all’interazione “parla”
  - \<scenario state= “nameState”>: path dello scenario da eseguire all’interazione “parla”
  
Di questi ultimi due tag ce ne possono essere in numero arbitrario, purché non ce ne siano due con lo stesso attributo
associato al valore “state”.

