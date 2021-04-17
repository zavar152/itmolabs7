package itmo.labs.zavar.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.JSchException;

public class DataBaseManager {

	private SshTunnel tunnel = null;
	private static String user, password, baseName;
	private static int localPort;
	private static Logger logger = LogManager.getLogger(DataBaseManager.class.getName());
	private static BasicDataSource ds = new BasicDataSource();

	public DataBaseManager(boolean ssh, String user, String password, String sshHost, String baseName, int sshPort,
			String remoteHost, int localPort, int remotePort) {

		DataBaseManager.user = user;
		DataBaseManager.localPort = localPort;
		DataBaseManager.password = password;
		DataBaseManager.baseName = baseName;

		if (ssh) {
			try {
				tunnel = new SshTunnel(user, password, sshHost, sshPort, remoteHost, localPort, remotePort);
				tunnel.connect();
				logger.info("Created ssh tunnel successfully");
			} catch (JSchException e) {
				e.printStackTrace();
				logger.error("Failed to open ssh tunnel");
			}
		}

	}

	public synchronized Connection getConnection() {
		return DBCPDataSource.getConnection();
	}

	public void stop() {

		DBCPDataSource.close();

		if (tunnel != null) {
			tunnel.disconnect();
		}
	}

	private static class DBCPDataSource {

		private static BasicDataSource ds = new BasicDataSource();

		static {
	        ds.setUrl("jdbc:postgresql://localhost:" + localPort + "/" + baseName);
	        ds.setUsername(user);
	        ds.setPassword(password);
	        ds.setMinIdle(5);
	        ds.setMaxIdle(10);
	        ds.setMaxOpenPreparedStatements(100);
		}
		
		public static Connection getConnection() {
			try {
				return ds.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("Failed to connect to database");
			}
			return null;
		}

		public static void close() {
			try {
				ds.close();
			} catch (SQLException e) {
				logger.error("Failed to close connection pool");
			}
		}

		private DBCPDataSource() {
		}
	}

	/*
	 * public ResultSet readFromDB(String query) throws SQLException { //Connection
	 * con = createConnection(); Statement stmt; stmt = con.createStatement();
	 * ResultSet rs = stmt.executeQuery(query); con.commit(); return rs; }
	 * 
	 * public void writeInDB(String sql) { //Connection con = createConnection();
	 * Statement stmt; try { stmt = con.createStatement(); stmt.executeUpdate(sql);
	 * stmt.close(); con.commit(); } catch (SQLException e) { e.printStackTrace(); }
	 * }
	 */

}
