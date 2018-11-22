package dao.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
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
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteEvents(String userId, List<String> eventIds) {

		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for setFavoriteEvents");
			}
			String sql = String.format("INSERT IGNORE INTO favorites (user_id, event_id) VALUES (\"%s\", ?)", userId);
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(String eventId : eventIds) {
				stmt.setString(1, eventId);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteEvents(String userId, List<String> eventIds) {
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for unsetFavoriteEvents");
			}

			String sql = String.format("DELETE FROM favorites WHERE user_id = %s AND event_id = ? ", userId);
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String eventId : eventIds) {
				stmt.setString(1, eventId);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getFavoriteEventIds(String userId) {
		Set<String> eventIds = new HashSet<>();
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for getFavoriteEventIds");
			}
			String sql = String.format("SELECT event_id FROM favorites WHERE user_id = '%s'", userId);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			while (result.next()) {
				eventIds.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return eventIds;
	}

	@Override
	public Set<Event> getFavoriteEvents(String userId) {
		Set<Event> events = new HashSet<>();
		Set<String> eventIds = getFavoriteEventIds(userId);
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for getFavoriteEventIds");
			}
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events WHERE event_id = ?");
			Event.Builder builder = new Event.Builder();
			for (String eventId : eventIds) {
				stmt.setString(1, eventId);
				ResultSet result = stmt.executeQuery();
				if (result.next()) {
					Event event = builder.setEventId(result.getString("event_id"))
							.setName(result.getString("name"))
							.setAddress(result.getString("address"))
							.setEventUrl(result.getString("event_url"))
							.setImgUrl(result.getString("image_url"))
							.setCategories(getCategoriesFromDB(eventId))
							.build();
					events.add(event);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return events;
	}

	@Override
	public void saveEvents(List<Event> events) {
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for getFavoriteEventIds");
			}
			PreparedStatement stmt1 = conn.prepareStatement(
					"INSERT IGNORE INTO events "
							+ "(event_id, name, address, event_url, image_url) "
							+ "VALUES (?, ?, ?, ?, ?)");
			PreparedStatement stmt2 = conn.prepareStatement(
					"INSERT IGNORE INTO categories (event_id, category) VALUES (?, ?)");
			for (Event event : events) {
				stmt1.setString(1, event.getEventId());
				stmt1.setString(2, event.getName());
				stmt1.setString(3, event.getAddress());
				stmt1.setString(4, event.getEventUrl());
				stmt1.setString(5, event.getImgUrl());
				stmt1.executeUpdate();

				stmt2.setString(1, event.getEventId());
				for (String category : event.getCategories()) {
					stmt2.setString(2, category);
					stmt2.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Set<String> getCategoriesFromDB(String eventId) {
		Set<String> categories = new HashSet<>();
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for getFavoriteEventIds");
			}
			String sql = String.format("SELECT category FROM categories WHERE event_id = '%s'", eventId);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			while (result.next()) {
				categories.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categories;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for getFavoriteEventIds");
			}
			String sql = String.format("SELECT * FROM users WHERE user_id = '%s' AND password = '%s'", userId, password);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			return result.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String getFullName(String userId) {
		String fullname = "";
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for getFavoriteEventIds");
			}
			String sql = String.format("SELECT first_name, last_name FROM users WHERE user_id = '%s'", userId);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			if (result.next()) {
				fullname = String.format("%s %s", result.getString(1), result.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fullname;
	}

	@Override
	public boolean isUserExist(String userId) {
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for isUserExist");
			}
			String sql = String.format("SELECT user_id FROM users WHERE user_id = '%s'", userId);
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sql);
			return result.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean createUser(String userId, String password) {
		try {
			if (conn == null || conn.isClosed()) {
				System.err.println("MySql connection status is" + conn);
				throw new SQLException("No existing connection for createUser");
			}
			String sql = String.format("INSERT IGNORE INTO users VALUES('%s', '%s', 'new-user', 'new-user')", userId, password);
			Statement stmt = conn.createStatement();
			return stmt.executeUpdate(sql) == 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


}
