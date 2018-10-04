package dao.mysql;

public class MySQLDBUtil {
	private final static String HOST_IP = "localhost";
	private final static String PORT_NUMBER = "3306";
	private final static String DATABASE_NAME = "tm_event_recommendation";
	private final static String USER_NAME = "****";
	private final static String PASSWORD = "****";
	final static String URL = String.format(
			"jdbc:mysql://%s:%s/%s?user=%s&password=%s&autoReconnect=true&serverTimezone=UTC",
			HOST_IP, PORT_NUMBER, DATABASE_NAME, USER_NAME, PASSWORD);
}
