import items.Door;
import rooms.Coordinates;
import rooms.Room;

public class HelloWorld
{
    public static void main(String[] args)
    {
        Door door1 = new Door(Door.OPEN);
        Door door2 = new Door(Door.CLOSED);
        Door door3 = new Door(Door.BLOCKED);

        Room bagnoStrano = new Room();
        bagnoStrano.addItem(door1, new Coordinates(100, 0));
        bagnoStrano.addItem(door2, new Coordinates(200, 100));
        bagnoStrano.addItem(door3, new Coordinates(400, 600));
    }

}
