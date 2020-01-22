package com.jobisjob.CounterBuffer.single;

import java.io.IOException;
import java.io.OutputStream;

import com.jobisjob.CounterBuffer.base.CounterAbstract;
import com.jobisjob.CounterBuffer.exception.FlushException;

/**
 * This writeBuffer is for test <br>
 * When an increment is called nothing will be done <br>
 * like /etc/null
 */
public class CounterBufferSingleOutputStream extends CounterBufferSingle<CounterSingle> {

	static final String SEPARATOR = ":";
	static final String NEW_LINE = "\n";
	
	OutputStream outputStream;
	
	public CounterBufferSingleOutputStream(int maxSize, long maxTimeInSeconds, OutputStream outputStream){
		super(maxSize, maxTimeInSeconds);
		this.outputStream = outputStream;
	}

	@Override
    public CounterSingle createElement(Comparable bufferKey) {
        return new CounterSingle(bufferKey);
    }

	@Override
	public void flushObj(CounterAbstract item) throws FlushException {
		CounterSingle singleItem = (CounterSingle)item;
		
		try {
	        outputStream.write( singleItem.getId().toString().getBytes() );
	        outputStream.write( SEPARATOR.getBytes() );
	        outputStream.write( String.valueOf( singleItem.getCount() ).getBytes() );
	        outputStream.write( NEW_LINE.getBytes() );
	        
        } catch (IOException e) {
	        throw new FlushException(e);
        }
	}

}
