package general;

/**
 * Classe che rappresenta una coppia di oggetti.
 *
 * Viene utilizzata per inventoryItemIconList (lista item-icone dell'inventario).
 *
 * @param <t1> primo tipo di oggetti della coppia
 * @param <t2> secondo tipo di oggetti della coppia
 */
public class Pair<t1, t2>
{
    private final t1 object1;
    private final t2 object2;

    public Pair(t1 object1, t2 object2)
    {
        this.object1 = object1;
        this.object2 = object2;
    }

    public t1 getObject1()
    {
        return object1;
    }

    public t2 getObject2()
    {
        return object2;
    }
}
