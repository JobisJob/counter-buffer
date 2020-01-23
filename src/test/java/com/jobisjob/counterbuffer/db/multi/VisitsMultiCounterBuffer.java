package com.jobisjob.counterbuffer.db.multi;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.jobisjob.counterbuffer.base.CounterAbstract;
import com.jobisjob.counterbuffer.exception.FlushException;
import com.jobisjob.counterbuffer.multi.CounterBufferMultiDataSource;
import com.jobisjob.counterbuffer.multi.CounterMulti;

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
