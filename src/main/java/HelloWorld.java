import characters.PlayingCharacter;
import items.Door;
import items.PickupableItem;
import rooms.Coordinates;
import rooms.Room;
import java.util.Scanner;

public class HelloWorld
{
    public static void main(String[] args)
    {
        PickupableItem a = new PickupableItem("oggetto strano", "fa cacare");
        PickupableItem chiaveSpicoli = new PickupableItem("Chiave spicola","Apre la porta bruna");

        PlayingCharacter.SPICOLO.addToInventory(a);
        PlayingCharacter.SPICOLO.addToInventory(chiaveSpicoli);

        Door door1 = new Door("Porta Spicola", Door.OPEN);
        Door door2 = new Door("Porta Santora", Door.CLOSED);
        Door door3 = new Door("Porta Bruna", Door.BLOCKED);
        Room bagnoStrano = new Room();
        bagnoStrano.addItem(door1, new Coordinates(100, 0));
        bagnoStrano.addItem(door2, new Coordinates(200, 100));
        bagnoStrano.addItem(door3, new Coordinates(400, 600));

        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        while(!input.equals("MUORI"))
        {
            System.out.println(door1.getState());
            System.out.println(door2.getState());
            System.out.println(door3.getState());
            System.out.println(":)  ----> :)");

            if(input.equals("APRI 1"))
                door1.open();
            else if(input.equals("APRI 2"))
                door2.open();
            else if(input.equals("APRI 3"))
                door3.open();
            else if(input.equals("CHIUDI 1"))
                door1.close();
            else if(input.equals("CHIUDI 2"))
                door2.close();
            else if(input.equals("CHIUDI 3"))
                door3.close();
            else if(input.equals("APRI 3 oggetto strano"))
                door3.unlock(a);
            else if (input.equals("APRI 3 chiave spicola"))
                door3.unlock(chiaveSpicoli);

            input = scanner.nextLine();
        }
    }

}
