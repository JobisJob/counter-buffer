package com.jobisjob.CounterBuffer.single;

import com.jobisjob.CounterBuffer.base.CounterAbstract;
import com.jobisjob.CounterBuffer.base.CounterBufferAbstract;

/**
 * Write Buffer to increment purpose
 */
public abstract class CounterBufferSingle<E extends CounterSingle> extends CounterBufferAbstract {


	/**
     * Constructor for WriteBuffer
     * @param maxSize size of the buffer
     * @param maxTimeInSeconds (in seconds)
     */
    public CounterBufferSingle(int maxSize, long maxTimeInSeconds) {
        super(maxSize, maxTimeInSeconds);
    }

    
    public void increment(Comparable bufferKey) {
        increment(bufferKey, 1);
    }
    
    public void increment(Comparable bufferKey, int value){
    	increment( bufferKey, null, value );
    }
    
    /**
     * Add again all counters 
     */
	@Override
	public void handleFlushException(CounterAbstract item, Exception e) {
		increment(item.getId(), ((CounterSingle)item).getCount() );
	}

	
	public E createElement(Comparable bufferKey) {
		return (E) new CounterSingle(bufferKey);
	}

    
    
}
    