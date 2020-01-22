package com.jobisjob.CounterBuffer.single;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;



public abstract class CounterBufferSingleDataSource extends CounterBufferSingle<CounterSingle> {

    private DataSource dataSource;
    
	private Connection connection = null;
	
	protected Object dao = null;
	
	
    
    public CounterBufferSingleDataSource(int maxSize, long maxTimeInSeconds, DataSource dataSource) {
        super(maxSize, maxTimeInSeconds);
        this.dataSource = dataSource;
    }
    

    
    protected void closeConnection(){
        try {
        	if(connection != null && !connection.isClosed()){
        		connection.close();
        		connection = null;
        	}
        } catch (SQLException e) {
            log.error("Error closing connection: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void afterFlush() {
    	closeConnection();
    	dao = null;
        
    }
    
    
    protected Connection getConnection() throws SQLException{
        if (connection == null) {
            connection = dataSource.getConnection();
        }
        return connection;
    }



}
