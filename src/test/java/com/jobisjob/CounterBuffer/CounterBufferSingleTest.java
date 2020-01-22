package com.jobisjob.CounterBuffer;

import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.jobisjob.CounterBuffer.single.CounterBufferSingleOutputStream;

import junit.framework.TestCase;

public class CounterBufferSingleTest extends TestCase{
	 
	
	protected Log log = LogFactory.getLog(CounterBufferSingleTest.class);
	
	public void testScheduled() throws Exception{
		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CounterBufferSingleOutputStream hbos = new CounterBufferSingleOutputStream(20, 1, baos);
		
		log.info( "start test" );
		
		hbos.increment( "alpha", 1 );
		hbos.increment( "beta", 1 );
		
		Thread.sleep( 2100 );
		
		log.info( hbos.getStats() );
		
		log.info( "assert" );
		log.info( "buffer to string: " + baos.toString() );
		assertEquals( "alpha:1\n" + "beta:1\n", baos.toString() );
		
		
		
		
	}
	
	@Test
	public void testSmallBuffer() throws InterruptedException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CounterBufferSingleOutputStream hbos = new CounterBufferSingleOutputStream(2, 500, baos);
		
		
		hbos.increment( "alpha", 1 );
		hbos.increment( "beta", 1 );
		
		Thread.sleep( 10 );
		
		hbos.increment( "alpha", 1 );
		
		System.out.println("stats: " + hbos.getStats() + "\n");
		hbos.flushAll();

		System.out.println(baos.toString());
		
		assertEquals( "alpha:1\n" + "beta:1\n" + "alpha:1\n" , baos.toString());
		
	}
	
	
	@Test
	public void testLoopOneFlush() throws InterruptedException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CounterBufferSingleOutputStream hbos = new CounterBufferSingleOutputStream(12, 500, baos);
		
		for (int i = 0; i < 100; i++) {
			hbos.increment( "a"+ (i%10), 1 );
		}
		
		System.out.println("stats: " + hbos.getStats() + "\n");
		hbos.flushAll();

		System.out.println(baos.toString());
		
		assertEquals( "a0:10\n" + 
				"a1:10\n" + 
				"a2:10\n" + 
				"a3:10\n" + 
				"a4:10\n" + 
				"a5:10\n" + 
				"a6:10\n" + 
				"a7:10\n" + 
				"a8:10\n" + 
				"a9:10\n", baos.toString());
		
	}

	@Test
	public void testLoop() throws InterruptedException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CounterBufferSingleOutputStream hbos = new CounterBufferSingleOutputStream(11, 500, baos);
		
		for (int i = 0; i < 20; i++) {
			hbos.increment( "a"+ (i%10), 1 );
			Thread.sleep( 10 );
		}
		
		System.out.println("stats: " + hbos.getStats() + "\n");
		hbos.flushAll();

		System.out.println(baos.toString());
		System.out.println("lines: " + baos.toString().split( "\n" ).length);
		assertNotEquals( baos.toString().split( "\n" ).length, 10);
		assertEquals( "a0:1\n" + 
				"a1:1\n" + 
				"a2:1\n" + 
				"a3:1\n" + 
				"a4:1\n" + 
				"a5:1\n" + 
				"a6:1\n" + 
				"a7:1\n" + 
				"a8:1\n" + 
				"a9:1\n" +
				"a0:1\n" + 
				"a1:1\n" + 
				"a2:1\n" + 
				"a3:1\n" + 
				"a4:1\n" + 
				"a5:1\n" + 
				"a6:1\n" + 
				"a7:1\n" + 
				"a8:1\n" + 
				"a9:1\n", baos.toString());
	}

}
