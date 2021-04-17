package itmo.labs.zavar.commands;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import itmo.labs.zavar.commands.base.Command;
import itmo.labs.zavar.commands.base.Environment;
import itmo.labs.zavar.db.DbUtils;
import itmo.labs.zavar.exception.CommandArgumentException;
import itmo.labs.zavar.exception.CommandException;
import itmo.labs.zavar.exception.CommandSQLException;

/**
 * Outputs information about the collection to the standard output stream.
 * Requires additional user input.
 * 
 * @author Zavar
 * @version 1.9
 * 
 */
public class InfoCommand extends Command {

	private InfoCommand() {
		super("info");
	}

	@Override
	public void execute(ExecutionType type, Environment env, Object[] args, InputStream inStream, OutputStream outStream) throws CommandException {
		if (args instanceof String[] && args.length > 0 && (type.equals(ExecutionType.CLIENT) || type.equals(ExecutionType.INTERNAL_CLIENT))) {
			throw new CommandArgumentException("This command doesn't require any arguments!\n" + getUsage());
		} else {
			super.args = args;
			if (type.equals(ExecutionType.SERVER) | type.equals(ExecutionType.SCRIPT)  | type.equals(ExecutionType.INTERNAL_CLIENT)) {
				PrintStream pr = ((PrintStream) outStream);
				try {
					Connection con = env.getDbManager().getConnection();
					PreparedStatement stmt;
					stmt = con.prepareStatement(DbUtils.getCount());
					ResultSet rs = stmt.executeQuery();
					int count = 0;
					String date = "N/A";
					while (rs.next()) {
						count = rs.getInt(1);
					}
					
					stmt = con.prepareStatement(DbUtils.getCreationDate());
					rs = stmt.executeQuery();
					while (rs.next()) {
						date = rs.getString(1);
					}
					con.close();
					pr.println("Database type: PostgreSQL");
					pr.println("Creation date: " + date);
					pr.println("Count of elements: " + count);
				} catch (SQLException e) {
					throw new CommandSQLException(e.getMessage());
				}
			}
		}
	}

	/**
	 * Uses for commands registration.
	 * 
	 * @param commandsMap Commands' map.
	 */
	public static void register(HashMap<String, Command> commandsMap) {
		InfoCommand command = new InfoCommand();
		commandsMap.put(command.getName(), command);
	}

	@Override
	public String getHelp() {
		return "This command shows information about the collection!";
	}
}
