package com.jobisjob.counterbuffer.base;

/** In this abstract class the actual counter is not defined, it will be defined in the children classes. <p>
 * Here we just define the Key of the cache as a {@link Comparable}
 * */
public abstract class CounterAbstract {
	
	/** creation time */
	protected long time;
	
	/** Key of the cache, can be any {@link Comparable}: <br>Integer, String... but also a complex object */
	protected Comparable bufferKey;
	

	public CounterAbstract(Comparable bufferKey) {
        time = System.currentTimeMillis();
        this.bufferKey = bufferKey;
    }
	
	
    public Comparable getId() {
        return bufferKey;
    }
	
	public long getTime() {
        return time;
    }
	
	public abstract void increment(Comparable field, int value);
    
}
