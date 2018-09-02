package dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLDBTableCreation {
	public static void main(String[] args) {
		Connection conn = null;
		try {
		Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
		conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (conn == null) {
			System.out.println("Failed to establish connection.");
			return;
		}
		
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DROP TABLE IF EXISTS categories");
			stmt.executeUpdate("DROP TABLE IF EXISTS favorites");
			stmt.executeUpdate("DROP TABLE IF EXISTS events");
			stmt.executeUpdate("DROP TABLE IF EXISTS users");
			
			String sql;
			
			sql = "CREATE TABLE events ("
					+ "event_id VARCHAR(255),"
					+ "name VARCHAR(255),"
					+ "address VARCHAR(255),"
					+ "image_url VARCHAR(255),"
					+ "event_url VARCHAR(255),"
					+ "PRIMARY KEY (event_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE categories (" 
					+ "event_id VARCHAR(255) NOT NULL,"
					+ "category VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (event_id, category),"
					+ "FOREIGN KEY (event_id) REFERENCES events(event_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "password VARCHAR(255) NOT NULL,"
					+ "first_name VARCHAR(255),"
					+ "last_name VARCHAR(255),"
					+ "PRIMARY KEY (user_id))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE favorites ("
					+ "user_id VARCHAR(255) NOT NULL,"
					+ "event_id VARCHAR(255) NOT NULL,"
					+ "PRIMARY KEY (user_id, event_id),"
					+ "FOREIGN KEY (event_id) REFERENCES events(event_id),"
					+ "FOREIGN KEY (user_id) REFERENCES users(user_id))";
			stmt.executeUpdate(sql);
			
			//step 4. insert data
			//using a fake user
			sql = "INSERT INTO users VALUES ("
					+ "'1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			System.out.println("Executing query: " + sql);
			stmt.executeUpdate(sql);
			
			System.out.println("Creation is done successfully.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
