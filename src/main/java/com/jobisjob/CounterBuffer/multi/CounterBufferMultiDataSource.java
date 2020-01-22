package com.jobisjob.CounterBuffer.multi;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;


public abstract class CounterBufferMultiDataSource extends CounterBufferMulti<CounterMulti> {

    private DataSource dataSource;
    
	private Connection connection = null;
	
	protected Object dao = null;
	
	
    
    public CounterBufferMultiDataSource(int maxSize, long maxTimeInSeconds, DataSource dataSource) {
        super(maxSize, maxTimeInSeconds);
        this.dataSource = dataSource;
    }
    

    
    protected void closeConnection(){
        try {
        	if(connection != null){
        		connection.close();
        		connection = null;
        	}
        } catch (SQLException e) {
            log.error("Error closing connection: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void afterFlush() {
        dao = null;
        closeConnection();
    }
    
    
    protected Connection getConnection() throws SQLException{
        if (connection == null) {
            connection = dataSource.getConnection();
        }
        return connection;
    }



}
