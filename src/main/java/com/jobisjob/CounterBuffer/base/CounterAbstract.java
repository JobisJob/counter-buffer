package com.jobisjob.CounterBuffer.base;

public abstract class CounterAbstract {

	protected long time;
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
