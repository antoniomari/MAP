import entity.characters.PlayingCharacter;
import entity.items.Door;
import entity.items.PickupableItem;
import entity.rooms.BlockPosition;
import entity.rooms.Room;

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


        door.setLocationRoom(null);


        currentRoom.printPieces();

        //door.setLocationRoom(currentRoom);
        //door.setPosition(new BlockPosition(50, 50));

        door.getPosition();


    }
}
