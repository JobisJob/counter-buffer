# CounterBuffer
Buffered counters for Java

Sometimes is useful to write counters in a database or a storage system. But, what if we need to continuously increment those counters? Maybe it's slow or a waste of resources.

Here comes the idea to have a <i>buffer of counters</i> with an automatic system to flush the increments when the buffer is too big or time is passed.

### Single counters
The basic usage is to have items and for every one of them having a single counter.
For example counting names repetitions; every time a name appears we increment hits counter.

---

Here an example on how to write a counterBuffer basic class:

```java
public class CountingNamesCounterBuffer extends CounterBufferSingleDataSource {

    public CountingNamesCounterBuffer(int maxSize, long maxTimeInSeconds, DataSource dataSource) {
        super( maxSize, maxTimeInSeconds, dataSource );
    }

    @Override
    public void flushObj( CounterAbstract item ) throws FlushException {

        try {
            CounterSingle myItem = (CounterSingle) item;

            Statement stat = getConnection().createStatement();
            try{
                stat.execute("insert into names "
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
VisitsSingleCounterBuffer visitsBuffer = new VisitsSingleCounterBuffer( 10, 120, dataSource );

visitsBuffer.increment( "George" );
visitsBuffer.increment( "Arthur");
visitsBuffer.increment( "George" );
```

In this example a counterBuffer with a buffer of size 10 is build. 
Every 120 seconds a flush thread will run. <br>
The third parameter (datasource) is a JdbcConnectionPool, CounterBuffer will get a connection to run the flush and will close is when finished.


---

### Multiple counters
Now, a more serious example.<br>
Imagine you need more than a single counter per every item.
For example counting per every item: impressions, views and clicks.

```java
VisitsMultiCounterBuffer visitsBuffer = new VisitsMultiCounterBuffer( 5, 1, dataSource );
		
visitsBuffer.increment( "firstItem", "impression", 1 );
visitsBuffer.increment( "firstItem", "impression", 1 );
visitsBuffer.increment( "firstItem", "view", 1 );
visitsBuffer.increment( "firstItem", "impression", 1 );
visitsBuffer.increment( "firstItem", "click", 1 );
visitsBuffer.increment( "secondItem", "impression", 1);
visitsBuffer.increment( "secondItem", "view", 1);
visitsBuffer.increment( "firstItem", "view", 1 );
visitsBuffer.increment( "secondItem", "impression", 1);
		
```

And here the code for the class to store in DB out counters

```java

public class VisitsMultiCounterBuffer extends CounterBufferMultiDataSource {

	public VisitsMultiCounterBuffer(int maxSize, long maxTimeInSeconds, DataSource dataSource) {
		super( maxSize, maxTimeInSeconds, dataSource );
	}

	@Override
	public void flushObj( CounterAbstract item ) throws FlushException {
		
		try {
			CounterMulti myItem = (CounterMulti) item;
			
			Statement stat = getConnection().createStatement();
			try{
				
				stat.execute("insert into visits_multi " +
						"values('" + myItem.getId() + "', " + 
						myItem.getCount("impression") + ", " + 
						myItem.getCount("view") +", " + 
						myItem.getCount("click") + ") " +
						"on duplicate key update " +
						"impression=impression+" +  myItem.getCount("impression") +
						", view=view+" +  myItem.getCount("view") +
						", click=click+" +  myItem.getCount("click")
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