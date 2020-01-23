package com.jobisjob.counterbuffer.multi;

import com.jobisjob.counterbuffer.base.CounterAbstract;
import com.jobisjob.counterbuffer.base.CounterBufferAbstract;

/**
 * Write Buffer to increment purpose
 */
public abstract class CounterBufferMulti<E extends CounterMulti> extends CounterBufferAbstract {

	
    /**
     * Constructor for WriteBuffer
     * @param maxSize size of the buffer
     * @param maxTime (in seconds)
     */
    public CounterBufferMulti(int maxSize, long maxTime) {
    	super(maxSize, maxTime);
    }
    
    public void increment(Comparable bufferKey, Comparable field) {
        increment(bufferKey, field, 1);
    }
    
    /**
     * Add again all counters 
     */
    @Override
    public void handleFlushException(CounterAbstract item, Exception e){
    	for (Comparable field: ((CounterMulti)item).getCounts().keySet()){
    		increment(item.getId(), field, ((CounterMulti)item).getCounts().get(field).get() );
    	}
    }
    
	public E createElement(Comparable bufferKey) {
		return (E) new CounterMulti(bufferKey);
	}    

    
    
}
    