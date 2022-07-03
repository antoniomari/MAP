package database;

import characters.PlayingCharacter;
import items.Item;
import items.PickupableItem;
import rooms.Coordinates;
import rooms.Room;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static void setupInventory() throws SQLException
    {

        startConnection();
        PreparedStatement pstm= conn.prepareStatement("SELECT nomeoggetto, descrizione FROM gamedb.inventario");
        ResultSet rs= pstm.executeQuery();
        while(rs.next())
        {
            PlayingCharacter.SPICOLO.addToInventory(new PickupableItem(rs.getString(1), rs.getString(2)));
        }
        rs.close();
        pstm.close();
        conn.close();
    }

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
}
