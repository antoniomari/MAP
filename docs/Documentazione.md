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
entity -> gui.

- la rappresentazione di input (gui -> entity) è possibile grazie alla classe PopMenuManager. 
Tramite il metodo addPieceOnScreen della classe GameScreenPanel, quando viene aggiunta la label con lo sprite di un 
GamePiece, viene creato un listener per il tasto destro che prevede la chiamata al metodo showMenu di PopMenuManager, 
passando sia il GamePiece che la label corrispondente. Il PopMenuManager crea un menu contestuale aggiungendo vari tasti 
corrispondenti alle varie interazioni possibili, in base alla classe effettiva del GamePiece passato. Infatti crea 
staticamente delle Action per ogni interazione possibile con qualsiasi GamePiece, in modo tale che cliccando sul tasto 
corrispondente venga eseguito il metodo relativo all’interazione per GamePiece sul quale si è cliccato.
Ad esempio, se il GamePiece è un’istanza di Item verrà abilitata una useAction in modo tale che venga chiamato il 
metodo Use dell’oggetto della classe Item.

## 2.6 Entity
Le classi entity rappresentano gli elementi di gioco non dipendenti dall’interfaccia grafica.
GamePiece
Un oggetto della classe GamePiece rappresenta un qualsiasi elemento fisico del gioco che può essere presente all’interno 
di una stanza (oggetti e personaggi). Ogni GamePiece ha un nome univoco ed uno stato, ovvero una stringa utilizzata per 
far si che un GamePiece abbia comportamenti diversi a seconda del suo valore.
Un GamePiece può avere un unico sprite, oppure uno sprite sheet descritto da un relativo file JSON. Può presentare una 
sequenza di immagini come specifica animazione (sono animate frames e perpetual animation frames per una specifica 
animazione perpetua) e inoltre può avere left moving frames e right moving frames utilizzati per le animazioni.

Nota: I frames delle perpetual animation possono essere presenti all’interno di una altro spritesheet con relativo json.

L'animazione perpetua inizia nel momento in cui il gamePiece è stampato sullo schermo e continua fino a quando l’oggetto 
è visualizzato. L’animazione viene eseguita su un thread dedicato ed in questo modo non influisce sugli altri elementi 
o sull’andamento del gioco. Gli animate frames sono utilizzati per la costruzione delle animazioni still animation 
quando vengono chiamati i metodi animate e animateReverse.
Un GamePiece ha una posizione nella stanza che viene misurata in blocchi (vedi blockPosition di Room) e corrisponde 
sempre alle coordinate del blocco in basso a sinistra del GamePiece (considerando il fatto che il blocco in alto a 
sinistra ha coordinate 0,0).

### 2.6.1 Item
Item è un GamePiece il cui sprite viene caricato dallo sprite-sheet e relativo JSON e presenta:
- Una descrizione opzionale
- Un’azione “usa” specializzata: il nome di tale azione può essere personalizzato, così come il suo effetto ed il numero 
di volte in cui può essere effettuata.

Implementando l’interfaccia Osservable avrà associato l’interazione osserva, che avrà come effetto mostrare la 
descrizione dell’oggetto.
I dati su tutti gli oggetti esistenti nel gioco sono presenti nel file oggetti.xml, questo file ha come root-tag 
oggetti all’interno del quale ci sono nodi xml con tag oggetto e attributo nome = nome dell’oggetto.
I dati degli oggetti sono descritti con i tag:
- <classe>: Classe effettiva dell’oggetto
- <descrizione>: Descrizione
- <canUse>: true se l’eventuale azione dell’oggetto è abilitata, false altrimenti
- <onUse>: contiene a sua volta i tag
  - <actionName>: nome dell’azione personalizzata
  - <useScenario state = “nameState”>: path dello scenario da eseguire quando l’oggetto viene usato (si clicca il 
  tasto corrispondente all’azione nel menu contestuale, o quando l’oggetto è nell’inventario, è selezionato e si clicca 
  sul giocatore).

Nota: ci possono essere più tag <useScenario> per associare comportamenti diversi a seconda dello stato.

- <animazionePerpetuaPng>: path dello spritesheet dell’animazione perpetua dell’oggetto
- <animazionePerpetuaJson>: path del file json relativo all’animazione perpetua dell’oggetto
- <onUsewith>: contiene a sua volta i tag
  - <keep>: se l’oggetto deve essere mantenuto nell’inventario dopo l’interazione useWith
  - <target>: GamePiece target dell’interazione useWith
  - <targetInitState>: è lo stato in cui deve trovarsi il target affinché l’interazione avvenga, quindi lo scenario venga eseguito
  - <targetFinalState>: lo stato in cui viene impostato il target alla fine dello scenario
  - <scenario>: path dello scenario associato all’interazione
- <onOpen>: che a sua volta contiene i tag:
  - <Scenario state = “nameState”>: path dello scenario da eseguire quando si chiama il metodo open


