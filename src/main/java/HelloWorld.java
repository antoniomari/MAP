import characters.PlayingCharacter;
import database.DBManager;
import items.Door;
import items.PickupableItem;
import rooms.Room;

import java.util.Scanner;

public class HelloWorld
{
    public static void main(String[] args) throws Exception
    {

        DBManager.setupInventory();

        Room cucina = DBManager.loadRoom("Cucina");
        Door door1, door2, door3;
        door1 = (Door) cucina.getItem(0);
        door2 = (Door) cucina.getItem(1);
        door3 = (Door) cucina.getItem(2);
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
                door3.unlock(PlayingCharacter.getPlayer().getInventory().get(0));
            else if (input.equals("APRI 3 chiave spicola"))
                door3.unlock(PlayingCharacter.getPlayer().getInventory().get(1));
            else if (input.equals("INV"))
                for (PickupableItem p: PlayingCharacter.getPlayer().getInventory())
                    System.out.println(p.getName() + " || " + p.getDescription());

            input = scanner.nextLine();

        }
    }

}
