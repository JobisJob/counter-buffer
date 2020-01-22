package com.jobisjob.CounterBuffer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class H2Utils {

	public H2Utils() {
	}
	
	public static void exec(DataSource dataSource, String cmd) throws SQLException{
		Connection conn = dataSource.getConnection();
        Statement stat = conn.createStatement();
        stat.execute(cmd);
        stat.close();
        conn.close();
	}

}
