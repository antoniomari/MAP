package general;

import java.util.Random;

public class Util
{
    public static <T>  T randomChoice(T[] array)
    {
        Random random = new Random();

        //numero da 0 a len-1
        return array[random.nextInt(array.length)];
    }
}
