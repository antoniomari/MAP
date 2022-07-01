package items;

public interface Lockable
{
    void lock();
    void unlock(PickupableItem key);
}
