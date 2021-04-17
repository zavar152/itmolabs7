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
import itmo.labs.zavar.exception.CommandRunningException;
import itmo.labs.zavar.exception.CommandSQLException;

/**
 * Removes one element from the collection whose studentsCount field value is
 * equivalent to the specified one. Requires student count.
 * 
 * @author Zavar
 * @version 1.2
 */
public class RemoveAnyBySCCommand extends Command {

	private RemoveAnyBySCCommand() {
		super("remove_any_by_students_count", "students_count");
	}

	@Override
	public void execute(ExecutionType type, Environment env, Object[] args, InputStream inStream, OutputStream outStream)
			throws CommandException {
		if (args instanceof String[] && args.length != 1 && (type.equals(ExecutionType.CLIENT) || type.equals(ExecutionType.INTERNAL_CLIENT))) {
			throw new CommandArgumentException("This command requires one argument!\n" + getUsage());
		} else {
			super.args = args;
			long sc;
			try {
				sc = Long.parseLong((String) args[0]);
			} catch (NumberFormatException e) {
				throw new CommandArgumentException("Students count shold be a long type!");
			} catch (Exception e) {
				throw new CommandRunningException("Unexcepted error! " + e.getMessage());
			}

			try {
				if (type.equals(ExecutionType.SERVER) | type.equals(ExecutionType.SCRIPT) | type.equals(ExecutionType.INTERNAL_CLIENT)) {
					Connection con = env.getDbManager().getConnection();
					PreparedStatement stmt;
					stmt = con.prepareStatement(DbUtils.getCount());
					ResultSet rs = stmt.executeQuery();
					rs.next();
					if (rs.getInt(1) == 0) {
						throw new CommandRunningException("Collection is empty!");
					}
					try {
						stmt = con.prepareStatement(DbUtils.deleteBySc(sc));
						if (stmt.executeUpdate() == 0) {
							((PrintStream) outStream).println("No such element!");
						} else {
							((PrintStream) outStream).println("Element deleted!");
						}
					} catch (Exception e) {
						throw new CommandRunningException("Unexcepted error! " + e.getMessage());
					}
					con.close();
				}
			} catch (SQLException e) {
				throw new CommandSQLException(e.getMessage());
			}
		}
	}

	/**
	 * Uses for commands registration.
	 * 
	 * @param commandsMap Commands' map.
	 */
	public static void register(HashMap<String, Command> commandsMap) {
		RemoveAnyBySCCommand command = new RemoveAnyBySCCommand();
		commandsMap.put(command.getName(), command);
	}

	@Override
	public String getHelp() {
		return "This command removes the element from collection if its students count equals to argument!";
	}
}
