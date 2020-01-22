# CounterBuffer
Buffered counters for Java

Sometimes is useful to write counters in a database. But, what if we need to continuously increment those counters? Maybe it's a waste of resources.

The idea is to have a <i>buffer of counters</i> with an automatic system to flush the increments when the buffer is too big or too much time is passed.

---

Here an example on how to write a counterBuffer class:

```java
public class VisitsSingleCounterBuffer extends CounterBufferSingleDataSource {

	public VisitsSingleCounterBuffer(int maxSize, long maxTimeInSeconds, DataSource dataSource) {
		super( maxSize, maxTimeInSeconds, dataSource );
	}

	@Override
	public void flushObj( CounterAbstract item ) throws FlushException {
		
		try {
			CounterSingle myItem = (CounterSingle) item;
			
			Statement stat = getConnection().createStatement();
			try{
				stat.execute("insert into visits "
						+ "values('" + myItem.getId() + "', " + myItem.getCount() + ") "
						+ "on duplicate key update count=count+" +  myItem.getCount()
						);
			}finally{
				stat.close();
			}
		} catch (SQLException e) {
			throw new FlushException( e );
		}
	}

}
```


And here how to use it:
```java
VisitsSingleCounterBuffer visitsBuffer = new VisitsSingleCounterBuffer( 100, 120, dataSource );
		
visitsBuffer.increment( "myKey" );
```

In this example a counterBuffer with a buffer of size 100 is build. 
Every 120 seconds a flush thread will run. <br>
The third parameter (datasource) is a JdbcConnectionPool, CounterBuffer will get a connection to run the flush and will close is when finished.

