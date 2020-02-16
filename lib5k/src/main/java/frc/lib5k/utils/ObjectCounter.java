package frc.lib5k.utils;

/**
 * A helper for counting the number of an object that exists
 */
public class ObjectCounter {

    private int count;

    public ObjectCounter() {
        this(0);
    }

    public ObjectCounter(int offset) {
        this.count = offset;
    }

    /**
     * Every time this is called, it will return the next int in the counter.
     * 
     * @return Next int
     */
    public int getNewID() {
        return count++;
    }

}