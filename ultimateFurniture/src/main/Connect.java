package main;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {
	private final String USERNAME = "root";
	private final String PASSWORD = "";
	private final String HOST = "localhost:3306";
	private final String DATABASE = "furnitureshop";
	private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);
	
	private static Connect instance = null;
	private Connection conn;
	
	private Connect() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
			System.out.println("Connection successful");
		} catch (Exception e) {
			System.out.println("Connection failed");
			e.printStackTrace();
		}
	}
	
	//singleton
	public static Connect getInstance() {
		if(instance == null) {
			instance = new Connect();
		}return instance;
	}
	
	//getconnection
	public Connection getConnection() {
		return conn;
	}
	
	//check run
	public static void main(String[] args) {
		Connect db = Connect.getInstance();
		if(db.getConnection() != null) {
			System.out.println("Yippie connection success");
		}else {
			System.out.println("Nah bro this aint it");
		}
	}
	

}
