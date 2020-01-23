package com.jobisjob.counterbuffer.single;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jobisjob.counterbuffer.base.CounterAbstract;

/**
 * Base counter class<p>
 * This class has a key and a counter, so we can put the name of the counter in the key and increment the counter calling {@link com.jobisjob.counterbuffer.single.CounterSingle#increment(int)}
 */
public class CounterSingle extends CounterAbstract implements Comparable<CounterSingle>{
	protected Log log = LogFactory.getLog(CounterSingle.class);
    private AtomicInteger count = new AtomicInteger(); 
    
    public CounterSingle(Comparable bufferKey) {
        super(bufferKey);
    }

    public void increment(int value) {
        count.addAndGet(value);
    }
    
	@Override
	public void increment( Comparable field, int value ) {
		increment( value );
		log.error( "In a " + getClass().getCanonicalName() + " is been called a method of a Multi incrementer" );
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
