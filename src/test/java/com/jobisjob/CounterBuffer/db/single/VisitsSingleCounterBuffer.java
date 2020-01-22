package com.jobisjob.CounterBuffer.db.single;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.jobisjob.CounterBuffer.base.CounterAbstract;
import com.jobisjob.CounterBuffer.exception.FlushException;
import com.jobisjob.CounterBuffer.single.CounterBufferSingleDataSource;
import com.jobisjob.CounterBuffer.single.CounterSingle;

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
