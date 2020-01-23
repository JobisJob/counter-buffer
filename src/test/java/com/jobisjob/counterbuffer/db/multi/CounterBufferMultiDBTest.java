package com.jobisjob.counterbuffer.db.multi;

import static org.junit.Assert.*;

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

import com.jobisjob.counterbuffer.H2Utils;


public class CounterBufferMultiDBTest {
	
	static DataSource dataSource;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		// remove previous file
		DeleteDbFiles.execute("~", "visits_multi", true);
		
		// prepare H2 pool
		Class.forName("org.h2.Driver");
		dataSource = JdbcConnectionPool.create("jdbc:h2:~/visits_multi;DB_CLOSE_ON_EXIT=FALSE;mode=MySQL", "sa", "sa");
		
		// create table
		H2Utils.exec(dataSource, "create table visits_multi(name varchar(255) primary key, impression int, view int, click int )" );
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// truncate table
		H2Utils.exec(dataSource, "truncate table visits_multi" );
		
	}


	@Test
	public void test() throws SQLException {
		VisitsMultiCounterBuffer visitsBuffer = new VisitsMultiCounterBuffer( 5, 1, dataSource );
		
		visitsBuffer.increment( "firstPage", "impression", 1 );
		visitsBuffer.increment( "firstPage", "impression", 1 );
		visitsBuffer.increment( "firstPage", "view", 1 );
		visitsBuffer.increment( "firstPage", "impression", 1 );
		visitsBuffer.increment( "firstPage", "click", 1 );
		visitsBuffer.increment( "secondPage", "impression", 1);
		visitsBuffer.increment( "secondPage", "view", 1);
		visitsBuffer.increment( "firstPage", "view", 1 );
		visitsBuffer.increment( "secondPage", "impression", 1);
		
		
		try {
			Thread.sleep( 1200 );
		} catch (InterruptedException e) {
		}
		
		
		Connection conn = dataSource.getConnection();
		Statement stat = conn.createStatement();
		
		ResultSet rs;
        rs = stat.executeQuery("select * from visits_multi");
        
        System.out.println( "----- DB table content -----" );
		while (rs.next()) {
			System.out.println( "name: " + rs.getString( "name" ) + 
					" impression: " + rs.getInt( "impression" ) +
					" view: " + rs.getInt( "view" ) +
					" click: " + rs.getInt( "click" ) ) ;

			final String name = rs.getString( "name" );
			if ("firstPage".equals( name )){
				assertEquals( 3, rs.getInt( "impression" ) );
			}else if ("secondPage".equals( name )){
				assertEquals( 1, rs.getInt( "view" ) );
			}else{
				fail();
			}

		}
		
        System.out.println( "----------" );
        stat.close();
        conn.close();
		
		
	}

}
