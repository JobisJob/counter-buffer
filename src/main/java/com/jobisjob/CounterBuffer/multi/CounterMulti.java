package com.jobisjob.CounterBuffer.multi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.jobisjob.CounterBuffer.base.CounterAbstract;

/**
 * Base class to the HitBuffer <br>
 * 
 */
public class CounterMulti extends CounterAbstract implements Comparable<CounterMulti>{
    private Map<Comparable,AtomicInteger> counts = new HashMap<Comparable, AtomicInteger>(); 
    
    public CounterMulti(Comparable bufferKey) {
        super(bufferKey);
    }

    public void increment(Comparable field, int value) {
    	AtomicInteger count = counts.get(field);
        if ( count == null){
        	count = new AtomicInteger();
        	counts.put(field, count);
        }
        count.addAndGet(value);
    }
    
    public int getCount(Comparable field) {
    	AtomicInteger count = counts.get(field);
    	if (count == null){
    		return 0;
    	}
        return count.get();
    }

    public Map<Comparable, AtomicInteger> getCounts(){
    	return counts;
    }
    
    @Override
    public String toString() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    	
        StringBuilder result = new StringBuilder();
        result.append("bufferKey:");
        result.append(bufferKey);
        result.append("; counts:[");
        
        String separator = "";
        for (Comparable field : counts.keySet()) {
        	result.append(separator);
            result.append(field);
            result.append(":");
            result.append(getCount(field));
            separator = ", ";
		}
        
        result.append("]");
        result.append("; time:");
        result.append(dateFormat.format(new Date(time)));        
        return result.toString();
    }

    public int compareTo(CounterMulti anotherHitBase) {
		return getId().compareTo(anotherHitBase.getId());
	}
}
