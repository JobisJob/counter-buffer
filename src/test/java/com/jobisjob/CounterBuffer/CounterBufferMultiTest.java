package com.jobisjob.CounterBuffer;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jobisjob.CounterBuffer.impl.TextIntKey;
import com.jobisjob.CounterBuffer.multi.CounterBufferMultiOutputStream;

import junit.framework.TestCase;

public class CounterBufferMultiTest extends TestCase{

	protected Log log = LogFactory.getLog( CounterBufferSingleTest.class );
	
	enum Event {Impression, View, CPC}
	
	public void testMultiBase() throws Exception {
		log.info( "start test" );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CounterBufferMultiOutputStream hbm = new CounterBufferMultiOutputStream( 10, 1, baos );

		hbm.increment( 123, Event.Impression, 5 );
		hbm.increment( 123, Event.View, 8 );

		hbm.flushAll();
		String output = baos.toString();
		
		assertTrue( output.contains( "Impression=5" ) );
		assertTrue( output.contains( "iew=8" ) );
		assertTrue( output.contains( "123" ) );
		
		log.info( "closing test" );
	}

	public void testMulti() throws Exception {
		log.info( "start test" );
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CounterBufferMultiOutputStream hbm = new CounterBufferMultiOutputStream( 10, 1, baos );

		hbm.increment( 123, new TextIntKey( Event.CPC.name(), 100 ), 5 );
		hbm.increment( 123, new TextIntKey( Event.CPC.name(), 75 ), 8 );

		hbm.flushAll();
		String output = baos.toString();
		
		assertTrue( output.contains( "[CPC-75]=8" ) );
		assertTrue( output.contains( "[CPC-100]=5" ) );
		assertTrue( output.contains( "123" ) );
		
		log.info( "closing test" );
	}
	
	
	
	
}
