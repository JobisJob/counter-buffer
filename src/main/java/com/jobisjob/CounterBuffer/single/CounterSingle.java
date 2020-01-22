package com.jobisjob.CounterBuffer.single;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.jobisjob.CounterBuffer.base.CounterAbstract;

/**
 * Base class to the HitBuffer <br>
 * 
 */
public class CounterSingle extends CounterAbstract implements Comparable<CounterSingle>{
    private AtomicInteger count = new AtomicInteger(); // Map<Comparable,AtomicInteger>
    
    public CounterSingle(Comparable bufferKey) {
        super(bufferKey);
    }

    public void increment(int value) {
        count.addAndGet(value);
    }
    
	@Override
	public void increment( Comparable field, int value ) {
		increment( value );
	}

    public int getCount() {
        return count.get();
    }

    @Override
    public String toString() {
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    	
        StringBuilder result = new StringBuilder();
        result.append("bufferKey:");
        result.append(bufferKey);
        result.append("; count:");
        result.append(count);
        result.append("; time:");
        result.append(dateFormat.format(new Date(time)));        
        return result.toString();
    }

    public int compareTo(CounterSingle anotherHitBase) {
		return getId().compareTo(anotherHitBase.getId());
	}

}
