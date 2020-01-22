package com.jobisjob.CounterBuffer.db.single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jobisjob.CounterBuffer.H2Utils;


public class CounterBufferSingleDBTest {
	
	static DataSource dataSource;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// remove previous file
		DeleteDbFiles.execute("~", "visits", true);
		
		// prepare H2 pool
		Class.forName("org.h2.Driver");
		dataSource = JdbcConnectionPool.create("jdbc:h2:~/visits;DB_CLOSE_ON_EXIT=FALSE;mode=MySQL", "sa", "sa");
		
		// create table
		H2Utils.exec(dataSource, "create table visits(name varchar(255) primary key, count int )" );
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// truncate table
		H2Utils.exec(dataSource, "truncate table visits" );
		
	}


	@Test
	public void test() throws SQLException {
		
		// create the counterBuffer with size 5 and 1 second of buffer life 
		VisitsSingleCounterBuffer visitsBuffer = new VisitsSingleCounterBuffer( 5, 1, dataSource );
		
		// use it 
		visitsBuffer.increment( "firstPage" );
		visitsBuffer.increment( "secondPage");
		visitsBuffer.increment( "firstPage" );
		
		// wait for the automatic flush
		try {
			Thread.sleep( 1200 );
		} catch (InterruptedException e) {
		}
		
		// check data in the DB 
		Connection conn = dataSource.getConnection();
		Statement stat = conn.createStatement();
		
		ResultSet rs;
        rs = stat.executeQuery("select * from visits");
        
        System.out.println( "----- DB table content -----" );
		while (rs.next()) {
			System.out.println( "name: " + rs.getString( "name" ) + " count: " + rs.getInt( "count" ) );

			final String name = rs.getString( "name" );
			if ("firstPage".equals( name )){
				assertEquals( 2, rs.getInt( "count" ) );
			}else if ("secondPage".equals( name )){
				assertEquals( 1, rs.getInt( "count" ) );
			}else{
				fail();
			}

		}
		
        System.out.println( "----------" );
        stat.close();
        conn.close();
		
		
	}

}
