package com.jobisjob.counterbuffer.db.single;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.jobisjob.counterbuffer.base.CounterAbstract;
import com.jobisjob.counterbuffer.exception.FlushException;
import com.jobisjob.counterbuffer.single.CounterBufferSingleDataSource;
import com.jobisjob.counterbuffer.single.CounterSingle;

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
