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

    private static  Connection conn;

    public static void setupInventory() throws SQLException
    {

        Connection conn = DriverManager.getConnection("jdbc:h2:./src/main/resources/test", "sa", "");
        PreparedStatement pstm= conn.prepareStatement("SELECT nomeoggetto, descrizione FROM inventario");
        ResultSet rs= pstm.executeQuery();
        while(rs.next())
        {
            PlayingCharacter.SPICOLO.addToInventory(new PickupableItem(rs.getString(1), rs.getString(2)));
        }
        rs.close();
        pstm.close();
    }

    public static Room loadRoom(String name) throws SQLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
        Connection conn = DriverManager.getConnection("jdbc:h2:./src/main/resources/test", "sa", "");
        PreparedStatement pstm= conn.prepareStatement("SELECT O.nome, O.classe, O.descrizione, D.x, D.y FROM stanza S JOIN disposizione D ON S.nome = D.nomestanza" +
                " JOIN oggetto O ON D.nomeoggetto = O.nome WHERE S.nome = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        pstm.setString(1,name);
        ResultSet rs= pstm.executeQuery();
        if (!rs.next())
            return null;

        rs.beforeFirst();

        Room room = new Room(name);
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
