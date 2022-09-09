package database;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import entity.items.PickupableItem;
import entity.rooms.BlockPosition;
import entity.rooms.Room;
import general.ActionSequence;
import general.GameException;
import general.GameManager;
import general.LogOutputManager;
import general.xml.XmlLoader;

import java.sql.*;
import java.util.List;

/**
 * Gestore per il DB di gioco, utilizzato per la memorizzazione
 * dei salvataggi.
 */
public class DBManager
{
    /** Connessione con database GAME. */
    private static Connection conn;
    /** Location del database per l'accesso. */
    private final static String DATABASE_PATH = "jdbc:h2:./db";
    /** Nomi di tabelle che non contengono FOREIGN KEY ad altre tabelle. */
    private final static String[] PURE_TABLE_NAMES = {"room", "item", "gameCharacter", "inventory"};
    /** Nomi di tabelle che contengono FOREIGN KEY verso le precedenti. */
    private final static String[] LINK_TABLE_NAMES = {"itemLocation", "characterLocation", "lockEntrance"};

    /**
     * Inizia la connessione al database di gioco.
     *
     * @throws SQLException se si verifica un errore nella connessione al db
     */
    private static void startConnection() throws SQLException
    {
        if (conn == null || !conn.isValid(0))
        {
            conn = DriverManager.getConnection(DATABASE_PATH, "sa", "");
        }
    }

    /**
     * Controlla se ci sono dei salvataggi all'interno del database di gioco.
     *
     * @return {@code true} se esistono dei salvataggi all'interno del database,
     * {@code false} altrimenti
     */
    public static boolean existSavings()
    {
        try
        {
            startConnection();

            // controlla se esiste db
            if(!existsDB())
                return false;

            // controlla se la tabella Room è vuota
            PreparedStatement roomStm = conn.prepareStatement("SELECT name, xmlPath, scenarioOnEnterPath FROM game.room");
            ResultSet rs = roomStm.executeQuery();

            // se ha almeno una tupla allora salvataggio presente
            boolean  result = rs.next();

            rs.close();
            roomStm.close();

            return result;

        }
        catch(SQLException e)
        {
            closeConnection();
            throw new Error(e);
        }
    }

    /**
     * Controlla se esiste il database di gioco (chiamato "GAME")
     *
     * @return {@code true} se esiste il database, {@code false} altrimenti
     */
    private static boolean existsDB()
    {
        try
        {
            startConnection();
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            ResultSet resultSet = databaseMetaData.getSchemas();

            // controlla il nome di tutti i database risultati
            while (resultSet.next())
            {
                String name = resultSet.getString("TABLE_SCHEM");

                // db presente
                if(name.equalsIgnoreCase("GAME"))
                    return true;
            }

            // db non presente
            return false;
        }
        catch(SQLException e)
        {
            closeConnection();
            throw new Error(e);
        }
    }

    /**
     * Chiude la connessione con il database di gioco
     */
    private static void closeConnection()
    {
        try
        {
            if(conn != null)
                conn.close();
        }
        catch(SQLException e)
        {
            throw new GameException("Impossibile chiudere la connessione con database per salvataggi");
        }

    }

    /**
     * Crea il database di gioco (col nome "GAME") se esso non esiste,
     * non fa nulla altrimenti
     */
    public static void createGameDB()
    {
        try
        {
            startConnection();

            if(existsDB())
            {
                LogOutputManager.logOutput("DB esistente", LogOutputManager.EVENT_COLOR);
                return;
            }

            LogOutputManager.logOutput("Creazione DB", LogOutputManager.EVENT_COLOR);
            // db non presente
            PreparedStatement createStatement = conn.prepareStatement("CREATE SCHEMA GAME");
            createStatement.executeUpdate();

            // crea tabelle
            PreparedStatement createTable = conn.prepareStatement(
                    "create table game.room\n" +
                    "(\n" +
                    "    name varchar(50),\n" +
                    "    xmlPath varchar(200),\n" +
                    "    scenarioOnEnterPath varchar(200),\n" +
                    "    primary key(name)\n" +
                    ");");
            createTable.executeUpdate();
            createTable = conn.prepareStatement(
                    "create table game.lockEntrance\n" +
                    "(\n" +
                    "    room varchar(50),\n" +
                    "    cardinal char(5),\n" +
                    "\n" +
                    "    primary key(room, cardinal),\n" +
                    "    foreign key (room) references room(name)\n" +
                    ");");
            createTable.executeUpdate();
            createTable = conn.prepareStatement(
                    "create table game.item\n" +
                            "(\n" +
                            "    name varchar(50),\n" +
                            "    state varchar(50),\n" +
                            "    canUse bit,\n" +
                            "    primary key(name)\n" +
                            ");");
            createTable.executeUpdate();
            createTable = conn.prepareStatement(
                    "create table game.gameCharacter\n" +
                            "(\n" +
                            "    name varchar(50),\n" +
                            "    state varchar(50),\n" +
                            "    primary key(name)\n" +
                            ");");
            createTable.executeUpdate();createTable = conn.prepareStatement(
                "create table game.itemLocation\n" +
                        "(\n" +
                        "    item varchar(50),\n" +
                        "    room varchar(50),\n" +
                        "    x int,\n" +
                        "    y int,\n" +
                        "    primary key (item),\n" +
                        "    foreign key (item) references item (name),\n" +
                        "    foreign key (room) references room (name)\n" +
                        ");");
            createTable.executeUpdate();createTable = conn.prepareStatement(
                "create table game.characterLocation\n" +
                        "(\n" +
                        "    gamecharacter varchar(50),\n" +
                        "    room varchar(50),\n" +
                        "    x int,\n" +
                        "    y int,\n" +
                        "    primary key (gamecharacter),\n" +
                        "    foreign key (gamecharacter) references gameCharacter (name),\n" +
                        "    foreign key (room) references room (name)\n" +
                        ");");
            createTable.executeUpdate();createTable = conn.prepareStatement(
                "create table game.inventory\n" +
                        "(\n" +
                        "    item varchar(50),\n" +
                        "    index int,\n" +
                        "    primary key (item)\n" +
                        ");");
            createTable.executeUpdate();

            LogOutputManager.logOutput("DB creato correttamente", LogOutputManager.EVENT_COLOR);

        } catch(SQLException e)
        {
            closeConnection();
            throw new Error(e);
        }
    }

    /**
     * Carica i salvataggi del gioco dal database.
     */
    public static void loadGameData()
    {
        try
        {
            startConnection();

            loadRooms();
            loadPieces();
            loadInventory();
        }
        catch(SQLException e)
        {
            closeConnection();
            throw new Error(e);
        }

    }

    /**
     * Carica nel GameManager i GamePiece dal database di gioco,
     * posizionandoli opportunamente nelle stanze (che devono già essere state caricate).
     *
     * @throws SQLException se si verifica un errore nel caricamento
     */
    public static void loadPieces() throws SQLException
    {
        PreparedStatement itemDataStm = conn.prepareStatement("SELECT name, state, canUse, room, x, y FROM game.item JOIN game.itemLocation ON name=item");
        ResultSet rs = itemDataStm.executeQuery();

        while(rs.next())
        {
            String name = rs.getString(1);
            String state = rs.getString(2);
            boolean canUse = rs.getBoolean(3);

            Item loadedItem = (Item) XmlLoader.loadPiece(name);
            loadedItem.setState(state);
            loadedItem.setCanUse(canUse);

            String roomName = rs.getString(4);
            int xPos = rs.getInt(5);
            int yPos = rs.getInt(6);

            loadedItem.addInRoom(GameManager.getRoom(roomName), new BlockPosition(xPos, yPos));
        }
        rs.close();
        itemDataStm.close();

        PreparedStatement charDataStm = conn.prepareStatement("SELECT name, state, room, x, y FROM game.gameCharacter JOIN game.characterLocation ON name=gameCharacter");
        ResultSet rs1 = charDataStm.executeQuery();

        while(rs1.next())
        {
            String name = rs1.getString(1);
            String state = rs1.getString(2);
            String roomName = rs1.getString(3);
            int xPos = rs1.getInt(4);
            int yPos = rs1.getInt(5);

            if(!name.equals(PlayingCharacter.getPlayerName()))
            {
                GameCharacter loadedCharacter = (GameCharacter) XmlLoader.loadPiece(name);
                loadedCharacter.setState(state);
                loadedCharacter.addInRoom(GameManager.getRoom(roomName), new BlockPosition(xPos, yPos));
            }
            else  // sei schwartz
            {
                PlayingCharacter.getPlayer().addInRoom(GameManager.getRoom(roomName), new BlockPosition(xPos, yPos));
            }
        }
        rs1.close();
        charDataStm.close();
    }

    /**
     * Carica nel GameManager le Room dal database di gioco, impostando
     * i collegamenti tra di esse (recuperando l'informazione dai file .xml delle stanze)
     * e bloccando le entrate a seconda del contenuto della tabella "LOCKENTRANCE".
     *
     * @throws SQLException se si verifica un errore nel caricamento
     */
    public static void loadRooms() throws SQLException
    {
        PreparedStatement roomStm = conn.prepareStatement("SELECT name, xmlPath, scenarioOnEnterPath FROM game.room");
        ResultSet rs = roomStm.executeQuery();
        while(rs.next())
        {
            String xmlPath = rs.getString(2);
            String scenarioOnEnterPath = rs.getString(3);

            Room loadedRoom = XmlLoader.loadRoom(xmlPath);
            loadedRoom.setScenarioOnEnter(scenarioOnEnterPath);

            ActionSequence loadedRoomInitScenario = XmlLoader.loadRoomInitDB(xmlPath);
            GameManager.startScenario(loadedRoomInitScenario);
        }
        rs.close();
        roomStm.close();

        // load roomLocks
        PreparedStatement lockStm = conn.prepareStatement("SELECT room, cardinal FROM game.lockEntrance");
        ResultSet lockResult = lockStm.executeQuery();

        while(lockResult.next())
        {
            String roomName = lockResult.getString(1);
            String cardinal = lockResult.getString(2);

            GameManager.getRoom(roomName).setAdjacentLocked(Room.Cardinal.fromString(cardinal), true);
        }

        lockStm.close();
        lockResult.close();
    }


    /**
     * Carica l'inventario dal database di gioco.
     *
     * @throws SQLException se si verifica un errore nel caricamento
     */
    public static void loadInventory() throws SQLException
    {
        PreparedStatement inventoryStm = conn.prepareStatement("SELECT item, index FROM game.inventory");
        ResultSet rs= inventoryStm.executeQuery();

        while(rs.next())
        {
            String name = rs.getString(1);
            PlayingCharacter.getPlayer().addToInventory((PickupableItem) XmlLoader.loadPiece(name));
        }
        rs.close();
        inventoryStm.close();
    }

    /**
     * Salva l'inventario nel database di gioco.
     *
     * @throws SQLException se si verifica un errore nel salvataggio
     */
    private static void saveInventory() throws SQLException
    {
        PreparedStatement inventoryStm = conn.prepareStatement("INSERT INTO game.inventory VALUES(?, ?)");

        for (PickupableItem it : PlayingCharacter.getPlayer().getInventory())
        {
            inventoryStm.setString(1, it.getName());
            inventoryStm.setInt(2, PlayingCharacter.getPlayer().
                                                   getInventory().indexOf(it));

            inventoryStm.executeUpdate();
        }
    }

    /**
     * Salva i GamePiece registrati nel GameManager all'interno del database,
     * nonché le loro posizioni all'interno delle stanze in cui sono contenuti.
     *
     * Nota: le stanze devono già essere state caricate nel database.
     *
     * @throws SQLException se si verifica un errore nel salvataggio
     */
    private static void saveGamePieces() throws SQLException
    {
        PreparedStatement stmItem = conn.prepareStatement("INSERT INTO game.item values(?, ?, ?)");
        PreparedStatement stmCharacters = conn.prepareStatement("INSERT INTO game.gamecharacter values(?, ?)");
        PreparedStatement stmItemLoc = conn.prepareStatement("INSERT INTO game.itemlocation values(?, ?, ?, ?)");
        PreparedStatement stmCharacterLoc = conn.prepareStatement("INSERT INTO game.characterlocation values(?, ?, ?, ?)");

        // ciclo che recupera ogni stanza dalla quale vengono prelevati oggetti e personaggi
        // per essere salvati nel database
        for(String r :  GameManager.getRoomNames())
        {
            List<GamePiece> list = GameManager.getRoom(r).getPiecesPresent();

            for( GamePiece gp : list )
            {

                PreparedStatement chosenStatement;

                if(gp instanceof Item)
                {
                    stmItem.setString(1,gp.getName());
                    stmItem.setString(2,gp.getState());
                    stmItem.setBoolean(3, ((Item) gp).canUse());
                    stmItem.executeUpdate();
                    chosenStatement = stmItemLoc;
                }
                else if(gp instanceof GameCharacter)
                {
                    stmCharacters.setString(1,gp.getName());
                    stmCharacters.setString(2,gp.getState());
                    stmCharacters.executeUpdate();
                    chosenStatement = stmCharacterLoc;
                }
                else
                {
                    throw new GameException("error");
                }

                chosenStatement.setString(1,gp.getName());
                chosenStatement.setString(2, r);
                chosenStatement.setInt(3,gp.getPosition().getX());
                chosenStatement.setInt(4,gp.getPosition().getY());
                chosenStatement.executeUpdate();
            }
        }
    }

    /**
     * Salva le stanze registrate nel GameManager all'interno del database
     * e la presenza di eventuali entrate bloccate (tabella lockEntrance)
     *
     * Nota: le stanze devono già essere state caricate nel database.
     *
     * @throws SQLException se si verifica un errore nel salvataggio
     */
    private static void saveRooms() throws SQLException
    {
        PreparedStatement roomStm = conn.prepareStatement("INSERT INTO game.room values(?, ?, ?)");
        PreparedStatement lockStm = conn.prepareStatement("INSERT INTO game.lockEntrance values(?, ?)");

        for (String name : GameManager.getRoomNames())
        {
            Room room = GameManager.getRoom(name);

            roomStm.setString(1,name);
            roomStm.setString(2, room.getXmlPath());
            roomStm.setString(3, room.getScenarioOnEnterPath());
            roomStm.executeUpdate();

            // salva locks
            for(Room.Cardinal cardinal : Room.Cardinal.values())
                if(room.isAdjacentLocked(cardinal))
                {
                    lockStm.setString(1, name);
                    lockStm.setString(2, String.valueOf(cardinal));
                    lockStm.executeUpdate();
                }
        }
    }

    /**
     * Elimina il contenuto di tutte le tabelle
     * del database GAME.
     *
     * @throws SQLException se si verifica un errore durante l'operazione
     */
    private static void deleteDatabaseContent() throws SQLException
    {
        PreparedStatement deleteStatement;
        for(final String TABLE : LINK_TABLE_NAMES)
        {
            deleteStatement = conn.prepareStatement("DELETE FROM game." + TABLE);
            deleteStatement.executeUpdate();
        }

        for(final String TABLE : PURE_TABLE_NAMES)
        {
            deleteStatement = conn.prepareStatement("DELETE FROM game." + TABLE);
            deleteStatement.executeUpdate();
        }
    }

    /**
     * Salva i dati di gioco all'interno del database
     * GAME.
     */
    public static void save()
    {
        try
        {
            startConnection();

            deleteDatabaseContent();

            saveRooms();
            saveGamePieces();
            saveInventory();
        }
        catch(SQLException e)
        {
            closeConnection();
            throw new Error(e);
        }

    }
}
