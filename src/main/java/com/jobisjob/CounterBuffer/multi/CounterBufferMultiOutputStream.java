package com.jobisjob.CounterBuffer.multi;

import java.io.IOException;
import java.io.OutputStream;

import com.jobisjob.CounterBuffer.base.CounterAbstract;
import com.jobisjob.CounterBuffer.exception.FlushException;


/**
 * This writeBuffer is a fake <br>
 * When an increment is called nothing will be done <br>
 * like /etc/null
 */
public class CounterBufferMultiOutputStream extends CounterBufferMulti<CounterMulti> {

	static final String SEPARATOR = ":";
	static final String NEW_LINE = "\n";
	
	OutputStream outputStream;
	
	public CounterBufferMultiOutputStream(int maxSize, long maxTimeInSeconds, OutputStream outputStream){
		super(maxSize, maxTimeInSeconds);
		this.outputStream = outputStream;
	}

	@Override
	public CounterMulti createElement(Comparable bufferKey) {
		return new CounterMulti(bufferKey);
	}

	@Override
	public void flushObj(CounterAbstract item) throws FlushException {
		//System.out.println(item.toString());
		CounterMulti multiItem = (CounterMulti)item;
		
		try {
	        outputStream.write( multiItem.getId().toString().getBytes() );
	        outputStream.write( SEPARATOR.getBytes() );
	        outputStream.write(  multiItem.getCounts().toString().getBytes() );
	        outputStream.write( NEW_LINE.getBytes() );
	        
        } catch (IOException e) {
	        throw new FlushException(e);
        }
		
	}



}
