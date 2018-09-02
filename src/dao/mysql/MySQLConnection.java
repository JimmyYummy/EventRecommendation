package dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import dao.DBConnection;
import entity.Event;

public class MySQLConnection implements DBConnection {
	private Connection conn;

	public MySQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteEvents(String userId, List<String> eventIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetFavoriteEvents(String userId, List<String> eventIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getFavoriteEventIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Event> getFavoriteEvents(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveEvents(List<Event> events) {
		// TODO Auto-generated method stub

	}

}
