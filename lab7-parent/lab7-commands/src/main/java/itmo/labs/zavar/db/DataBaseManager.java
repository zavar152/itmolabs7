package itmo.labs.zavar.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.JSchException;

public class DataBaseManager {

	private SshTunnel tunnel = null;
	private String user, password, baseName;
	private int localPort;
	private Logger logger = LogManager.getLogger(DataBaseManager.class.getName());
	private ExecutorService readerService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
	private ExecutorService writerService = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

	public DataBaseManager(boolean ssh, String user, String password, String sshHost, String baseName, int sshPort,
			String remoteHost, int localPort, int remotePort) {

		this.user = user;
		this.localPort = localPort;
		this.password = password;
		this.baseName = baseName;

		if (ssh) {
			try {
				tunnel = new SshTunnel(user, password, sshHost, sshPort, remoteHost, localPort, remotePort);
				tunnel.connect();
			} catch (JSchException e) {
				e.printStackTrace();
				logger.error("Failed to open ssh tunnel");
			}
			logger.info("Created ssh tunnel successfully");
		}

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			logger.error("Failed to find the driver");
		}
		logger.info("Driver loaded successfully");
	}

	private Connection createConnection() {
		Connection con = null;

		try {
			con = DriverManager.getConnection("jdbc:postgresql://localhost:" + localPort + "/" + baseName, user,
					password);
			con.setAutoCommit(false);
		} catch (SQLException e) {
			logger.error("Failed to connect to database");
		}
		logger.info("Connected to database successfully");

		return con;
	}

	public void stop() {
		readerService.shutdownNow();
		writerService.shutdownNow();
		
		if(tunnel != null)
		{
			tunnel.disconnect();
		}
	}
	
	public Future<ResultSet> readFromDB(String query) throws SQLException {
		return readerService.submit(() -> {
			Connection con = createConnection();
			Statement stmt;
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			con.commit();
			return rs;
		});
	}
	
	public Future<Boolean> writeInDB(String sql) {
		return writerService.submit(() -> {
			Connection con = createConnection();
			Statement stmt;
			try {
				stmt = con.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
				con.commit();
			} catch (SQLException e) {
				return false;
			}
			return true;
		});
	}

}
