package database;

import entity.GamePiece;
import entity.characters.GameCharacter;
import entity.characters.NPC;
import entity.characters.PlayingCharacter;
import entity.items.Item;
import entity.items.PickupableItem;
import entity.rooms.Room;
import general.GameException;
import general.GameManager;
import general.LogOutputManager;
import org.h2.command.Prepared;

import java.io.IOError;
import java.security.Key;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBManager
{

    private static Connection conn;
    private final static String DATABASE_PATH = "jdbc:h2:./db/gamedb";


    private static void startConnection() throws SQLException
    {
        if (conn == null || !conn.isValid(0))
        {
            conn = DriverManager.getConnection(DATABASE_PATH, "sa", "");
        }
    }

    /*
    private static void process(PreparedStatement pstm) throws SQLException
    {
        startConnection();
        ResultSet rs = pstm.executeQuery();
        while (rs.next())
        {

        }
        rs.close();
        pstm.close();
        conn.close();
    }
    */

    public static void setupInventory()
    {
        try
        {
            startConnection();
            PreparedStatement pstm= conn.prepareStatement("SELECT nomeoggetto, descrizione FROM gamedb.inventario");
            ResultSet rs= pstm.executeQuery();
            while(rs.next())
            {
                PlayingCharacter.getPlayer().addToInventory(new PickupableItem(rs.getString(1), rs.getString(2), false));
            }
            rs.close();
            pstm.close();
            conn.close();
        }
        catch (SQLException e)
        {
            LogOutputManager.logOutput(e.getMessage(), LogOutputManager.EXCEPTION_COLOR);
            throw new IOError(e); // TODO: migliorare
        }

    }

    private static void saveInventory() throws SQLException {
        PreparedStatement pstmInventory = conn.prepareStatement("INSERT INTO inventory VALUES(?, ?");

        // cicla sugli oggetti recuperati dal Inventory e per ogni oggetto prepara la
        // query da eseguire per il salvataggio
        for (Item it : PlayingCharacter.getPlayer().getInventory())
        {
            pstmInventory.setString(1, it.getName());
            pstmInventory.setInt(2, PlayingCharacter.getPlayer().
                                                   getInventory().indexOf(it));

            ResultSet rsINV = pstmInventory.executeQuery();
        }
    }

    private static void saveGamePices() throws SQLException
    {
        // gli statemente servo per preparere le diverse operazioni di aggiunta dati al database
        PreparedStatement pstmItem = conn.prepareStatement("INSERT INTO item values(?, ?, ?)");
        PreparedStatement pstmCharacters = conn.prepareStatement("INSERT INTO gamecharacter values(?, ?)");
        PreparedStatement pstmItemLoc = conn.prepareStatement("INSERT INTO itemlocation values(?, ?, ?, ?");
        PreparedStatement pstmCharacterLoc = conn.prepareStatement("INSERT INTO characterlocation values(?, ?, ?, ?");

        // ciclo che recupera ogni stanza dalla quale vengono prelevati oggetti e personaggi
        // per essere salvati nel database
        for(String r :  GameManager.getRoomNames())
        {
            List<GamePiece> list = GameManager.getRoom(r).getPiecesPresent();

            for( GamePiece gp : list ) {

                PreparedStatement chosenStatement;

                if(gp instanceof Item) {
                    pstmItem.setString(1,gp.getName());
                    pstmItem.setString(2,gp.getState());
                    if(((Item)gp).canUse())
                    {
                        pstmItem.setBoolean(3, true);
                    }
                    else
                    {
                        pstmItem.setBoolean(3, false);
                    }
                    ResultSet rsPIT = pstmItem.executeQuery();
                    chosenStatement = pstmItemLoc;
                }
                else if(gp instanceof GameCharacter) {
                    pstmCharacters.setString(1,gp.getName());
                    pstmCharacters.setString(2,gp.getState());
                    ResultSet rsPCH= pstmCharacters.executeQuery();
                    chosenStatement = pstmCharacterLoc;
                }
                else
                {
                    throw new GameException("error");
                }

                chosenStatement.setString(1,gp.getName());
                chosenStatement.setString(2, r);
                chosenStatement.setInt(3,gp.getPosition().getX());
                chosenStatement.setInt(4,gp.getPosition().getY());
                ResultSet rsPIC = chosenStatement.executeQuery();
            }
        }
    }

    public static void saveRooms() throws SQLException
    {
        startConnection();
        PreparedStatement pstm1= conn.prepareStatement("INSERT INTO room values(?, ?)");

        for (String name : GameManager.getRoomNames())
        {
            pstm1.setString(1,name);
            pstm1.setString(2, GameManager.getRoom(name).getScenarioOnEnterPath());
            ResultSet rs1= pstm1.executeQuery();
        }

        saveGamePices();
        saveInventory();

        pstm1.close();
        conn.close();



        /*
        if (!rs1.next())
            return null;

        Room room = new Room(name, rs1.getString(1));
        rs1.close();
        pstm1.close();

        PreparedStatement pstm= conn.prepareStatement("SELECT O.nome, O.classe, O.descrizione, D.x, D.y FROM gamedb.stanza S JOIN gamedb.disposizione D ON S.nome = D.nomestanza" +
                " JOIN gamedb.oggetto O ON D.nomeoggetto = O.nome WHERE S.nome = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        pstm.setString(1,name);
        ResultSet rs= pstm.executeQuery();
        if (!rs.next())
            return null;

        rs.beforeFirst();

        Item item;
        Class itemClass;

        while(rs.next())
        {
            itemClass = Class.forName("items." + rs.getString(2));

            Constructor<?> cons = itemClass.getConstructor(String.class, String.class);
            item = (Item) cons.newInstance(rs.getString(1), rs.getString(3));
            room.addItem(item, new Coordinates(rs.getInt(4), rs.getInt(5)));
        }

        rs.close();
        pstm.close();
        conn.close();

        return room;

         */
    }



    /*
    public static Room loadRoom(String name) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        startConnection();
        PreparedStatement pstm1= conn.prepareStatement("SELECT path FROM gamedb.stanza WHERE nome = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        pstm1.setString(1,name);
        ResultSet rs1= pstm1.executeQuery();
        if (!rs1.next())
            return null;

        Room room = new Room(name, rs1.getString(1));
        rs1.close();
        pstm1.close();

        PreparedStatement pstm= conn.prepareStatement("SELECT O.nome, O.classe, O.descrizione, D.x, D.y FROM gamedb.stanza S JOIN gamedb.disposizione D ON S.nome = D.nomestanza" +
                " JOIN gamedb.oggetto O ON D.nomeoggetto = O.nome WHERE S.nome = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        pstm.setString(1,name);
        ResultSet rs= pstm.executeQuery();
        if (!rs.next())
            return null;

        rs.beforeFirst();

        Item item;
        Class itemClass;

        while(rs.next())
        {
            itemClass = Class.forName("items." + rs.getString(2));

            Constructor<?> cons = itemClass.getConstructor(String.class, String.class);
            item = (Item) cons.newInstance(rs.getString(1), rs.getString(3));
            room.addItem(item, new Coordinates(rs.getInt(4), rs.getInt(5)));
        }

        rs.close();
        pstm.close();
        conn.close();

        return room;
    }

     */
}
