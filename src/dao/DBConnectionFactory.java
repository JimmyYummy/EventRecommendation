package dao;

public class DBConnectionFactory {
	private static final String DEAFULT_DB = "mysql";
	
	public static DBConnection getConnection() {
		return getConnection(DEAFULT_DB);
	}
	
	public static DBConnection getConnection(String db) {
		switch(db) {
		case "mysql":
			return null;
		default:
			throw new IllegalArgumentException("Invild DB: " + db);
		}
	}
}
