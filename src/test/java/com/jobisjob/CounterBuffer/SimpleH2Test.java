package com.jobisjob.CounterBuffer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import org.junit.Test;

import junit.framework.TestCase;

public class SimpleH2Test extends TestCase {


	@Test
	public void test() {
		
      // delete the database named 'test' in the user home directory
      DeleteDbFiles.execute("~", "test", true);
      try{
          Class.forName("org.h2.Driver");
          //Connection conn = DriverManager.getConnection("jdbc:h2:~/test");
          
          DataSource cp = JdbcConnectionPool.create("jdbc:h2:~/test;mode=MySQL", "sa", "sa");
          
          Connection conn = cp.getConnection();
          
          Statement stat = conn.createStatement();
          
  
          // this line would initialize the database
          // from the SQL script file 'init.sql'
          // stat.execute("runscript from 'init.sql'");
  
          stat.execute("create table test(name varchar(255) primary key, count int )");
          stat.execute("insert into test values('MyCounter', 1)");
          stat.execute("insert into test values('MyCounter', 1) on duplicate key update count=count+1");
          ResultSet rs;
          rs = stat.executeQuery("select * from test");
          while (rs.next()) {
        	  System.out.println("name: " + rs.getString( "name" ) + " - value: " + rs.getInt( "count" ));
              
        	  if (rs.getString( "name").equals( "MyCounter") ){
        		  assertEquals( 2, rs.getInt( "count" ) );
        	  }
          }
          stat.close();
          conn.close();
      }catch (Exception e) {
			// TODO: handle exception
		}
	}

}
