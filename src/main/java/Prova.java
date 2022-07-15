import characters.PlayingCharacter;
import items.Door;
import items.PickupableItem;
import rooms.BlockPosition;
import rooms.Room;

public class Prova
{


    public static void main(String[] args)
    {
        Room currentRoom = new Room("Cucina", "/img/lab1 griglia.png", "/img/lab1.json");
        PickupableItem barile1 = new PickupableItem(
                "Barile", "Un barile scemo come Basile", currentRoom);

        barile1.setPosition(new BlockPosition(18, 12));

        Door door = new Door("Porta", "Una porta spicolosa.");
        door.setLocationRoom(currentRoom);
        door.setPosition(new BlockPosition(14, 7));

        PlayingCharacter.getPlayer().setLocationRoom(currentRoom);
        PlayingCharacter.getPlayer().setPosition(new BlockPosition(12, 10));


        currentRoom.printPieces();

    }
}
