package com.jobisjob.CounterBuffer.db.multi;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.jobisjob.CounterBuffer.base.CounterAbstract;
import com.jobisjob.CounterBuffer.exception.FlushException;
import com.jobisjob.CounterBuffer.multi.CounterBufferMultiDataSource;
import com.jobisjob.CounterBuffer.multi.CounterMulti;

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
