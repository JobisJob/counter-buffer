package com.jobisjob.CounterBuffer.base;


import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jobisjob.CounterBuffer.exception.FlushException;
import com.jobisjob.executor.DefaultNamedThreadFactory;


public abstract class CounterBufferAbstract<E extends CounterAbstract> implements Runnable {
	
	protected Log log = LogFactory.getLog(CounterBufferAbstract.class);
	protected static final long OVERFLOW_SLEEP_MILLIS = 10;
    
	/** set to thue to stop the flush loop */
    protected boolean stopThread = false;

    protected final ConcurrentHashMap<Comparable, E> map = new ConcurrentHashMap<Comparable, E>();   
    protected final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
    
    protected int maxSize;
    /** Time in millis */
    protected long maxTime;
    
    protected AtomicLong incCounter = new AtomicLong();
    protected AtomicLong hitCounter = new AtomicLong();
    protected AtomicInteger flushCounter = new AtomicInteger();
    protected AtomicInteger itemFlushedCounter = new AtomicInteger();
    
    protected Thread shutdownThread;

    protected int flushTriggerOn;
    protected int flushTriggerOff;
    
    private final long startTime = System.currentTimeMillis();
    
    
    Semaphore flushSemaphore = new Semaphore(1);
    
    /** Override this method to flush an object 
     * @param item The object to flush
     * @throws FlushException when is not possible flush the object
     */
    public abstract void flushObj(E item) throws FlushException;
    
	public abstract void handleFlushException(E item, Exception e);
    
	/** Override this method to create the value element of the buffer
	 * @param id key for the counter
	 * @return the counter created  
	 */
    public abstract E createElement(Comparable id);
    
    /** Runs flush every few seconds */
    private ScheduledExecutorService scheduledExecutorService;
    
    public E getValue(Comparable bufferKey){
    	return map.get(bufferKey);
    }
    
	
    public CounterBufferAbstract(int maxSize, long maxTimeInSeconds) {
        this.maxSize = maxSize;
        this.maxTime = maxTimeInSeconds*1000;
        
        flushTriggerOn = Math.max(1,(int) (maxSize * 0.9f));
        flushTriggerOff = Math.max(1,(int) (maxSize * 0.8f));

        // ShutdownHook
        shutdownThread = new ShutMeDownThread();
        Runtime.getRuntime().addShutdownHook(shutdownThread);
        
        
        ThreadFactory threadFactory = new DefaultNamedThreadFactory( this.getClass().getSimpleName() );
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
        
        scheduledExecutorService.scheduleAtFixedRate( this, 
        		maxTimeInSeconds, maxTimeInSeconds, TimeUnit.SECONDS);
        
    } 
    
    private class ShutMeDownThread extends Thread{
        public void run() {
        	
            log.debug("shutdown started: " + getSize() + " items to save" );
            
            flushAll(); 
        }
    }
    
    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    public final void run() {
    	flushPartial();
    }
    
    
    public final void flushPartial() {
        if (stopThread){
        	log.debug( "Thread stopped" );
        	return;
        }
        
        if (queue.size() <= 0) {
            return;
        }
        E item;
        
    	log.debug( "Thread started" );
    	int itemFlushedForThisLoop = 0;
        
        try {
        	flushSemaphore.acquire();
        	
            do {
                item = null;
                synchronized (this) {
                    // select the obj to flush
                    if (queue.size() > flushTriggerOff) {
                    	
                    	// remove the obj from the map and from the queue
                        item = queue.poll();
                        map.remove(item.getId());
                    } else {
                        long timeGap = System.currentTimeMillis() - maxTime;

                        if (queue.peek() != null && queue.peek().getTime() < timeGap) {
                        	
                        	// remove the obj from the map and from the queue
                            item = queue.poll();
                            map.remove(item.getId());
                            
                        }
                    }

                }
                
                if (item != null) {

                	if (log.isTraceEnabled()) {
                        log.trace("thread flushing " + item);
                    }

                    try {
                        flushObj(item);
                        itemFlushedForThisLoop++;
                    } catch (FlushException e) {
                    	log.error("Error flushing  - item reinserting: " + item, e);
                    	handleFlushException(item, e);
                    }
                }
            } while (item != null && !stopThread);
            
            if (itemFlushedForThisLoop > 0){
            	flushCounter.incrementAndGet();
            	itemFlushedCounter.set(itemFlushedForThisLoop);
            }
        } catch (InterruptedException e1) {
			log.error( e1.getMessage(), e1 );
			
		} finally {
			afterFlush();
			flushSemaphore.release();
        	log.debug( "Thread finished" );
            
        }
    }

    /**
     * Write all items 
     * 
     */
    public void flushAll(){
    	stopThread = true;
    	awaitTerminationAfterShutdown(scheduledExecutorService);
    	
    	log.debug( "flushAll (thread stopped)" );
    	flushCounter.incrementAndGet();
    	
    	E item;
    	try {
    		/* the acquire is not good for us because the shutdown method of the ThreadPoolExecutor call an interrupt,
    		 * we want this method to finish anyway */
    		flushSemaphore.acquireUninterruptibly();
			
			itemFlushedCounter.set(0);
			while (!queue.isEmpty()) {
				synchronized (this) {
					item = queue.poll();
					map.remove(item.getId());
				}
			    try {					
					flushObj(item);
					itemFlushedCounter.incrementAndGet();
				} catch (FlushException e) {
			    	log.error("Error flushing  - item reinserting: " + item, e);
			    	handleFlushException(item, e);
				}
			}
			
		} finally {
			afterFlush();
			stopThread = false;
			flushSemaphore.release();
			
		}
        
    }
	
	/**
     * Determines if we need to run the flush thread
     * @return is true when is necesary to run a flush method  
     */
    protected boolean needCheck() {
    	return (map.size() > flushTriggerOn);
    }

    
    public final String toString() {
    	StringBuilder str = new StringBuilder();

    	
    	Map<String, String> stats = getStats();
    	for(String key : stats.keySet()){
    		str.append(" " + key + ":" + stats.get(key));
    	}
    	
    	return str.toString();
    }
    

    public Map<String, String> getStats() {
    	Map<String,String> stats = new LinkedHashMap<String,String>();
    	stats.put("Size (queue/map)" , String.valueOf(getSize()) + "/" + map.size());
    	stats.put("StartTime" , String.valueOf(new Date(startTime)));
    	stats.put("FlushAvgPeriodSec" , String.valueOf(getFlushAvgPeriod()) );
    	stats.put("MaxSize", String.valueOf(maxSize));
    	stats.put("MaxTimeSec", String.valueOf(maxTime/1000));
    	stats.put("Increments", String.valueOf(incCounter.get()));
    	stats.put("Hits",  String.valueOf(hitCounter.get()));
    	stats.put("HitRatio", String.valueOf( (hitCounter.get()*100f) / incCounter.get()) + "%");
    	stats.put("Flushes", String.valueOf(flushCounter.get()));
    	stats.put("ItemFlushed", String.valueOf(itemFlushedCounter.get()));
    	stats.put("FlushTriggerOn", String.valueOf(flushTriggerOn));
    	stats.put("FlushTriggerOff", String.valueOf(flushTriggerOff));
    	return stats;
    }  
    
    
    private float getFlushAvgPeriod(){
    	long time = System.currentTimeMillis();
    	return (time - startTime)/(flushCounter.get()*1000f);
    }
    
    
    
    
    public void increment(Comparable bufferKey, Comparable field, int value) {
		incCounter.incrementAndGet();
		E item = null;
		boolean inserted = false;
		
		while(!inserted){
    		synchronized (this) {
    			item = (E) getValue(bufferKey);
    			if (item != null) {
    				// we have the element
    				item.increment(field, value);
    				hitCounter.incrementAndGet();
    				inserted = true;
    			} else {
    				// the element not exists
    				if (map.size() < maxSize){
    					
    					item = createElement(bufferKey);
    					item.increment(field, value);
    					map.put(bufferKey, item);
    					queue.add(item);
    					inserted = true;
    				}
    				// else => overflow
    			}
    		}
    
    		
    		// if needed run a partial flush
    		if (needCheck() || !inserted) {
    			if (!scheduledExecutorService.isShutdown()){
    				scheduledExecutorService.execute( this );
    			}else{
    				log.warn( "Impossible run partial flush while shuting down" );
    			}
            }

    		if (!inserted){
    			try {
	                Thread.sleep( OVERFLOW_SLEEP_MILLIS );
                } catch (InterruptedException e) {
                }
    		}
		}
	}

    
	/**
	 * Override if needed
	 */
	public void afterFlush() {
	}    


    
    /**
     * Shutdown the writebuffer without flushing items in the queue
     */
    public final void terminate() {
        log.trace("terminate method called");
        if (shutdownThread != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
        }
    }
    

    
	public int getFlushCounter() {
		return flushCounter.intValue();
	}
    
	public int getLastFlushedCounter() {
		return itemFlushedCounter.intValue();
	}
	
    public int getSize() {
    	return queue.size();
    }
	
    public final String valuesToString() {
    	return queue.toString();
    }

	public void setFlushTriggerOn( int flushTriggerOn ) {
		this.flushTriggerOn = flushTriggerOn;
	}

	public void setFlushTriggerOff( int flushTriggerOff ) {
		this.flushTriggerOff = flushTriggerOff;
	}

	public int getFlushTriggerOn() {
		return flushTriggerOn;
	}

	public int getFlushTriggerOff() {
		return flushTriggerOff;
	}
	

}
