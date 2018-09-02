package dao;

import dao.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEAFULT_DB = "mysql";
	
	public static DBConnection getConnection() {
		return getConnection(DEAFULT_DB);
	}
	
	public static DBConnection getConnection(String db) {
		switch(db) {
		case "mysql":
			return new MySQLConnection();
		default:
			throw new IllegalArgumentException("Invild DB: " + db);
		}
	}
}
