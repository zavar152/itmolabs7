package itmo.labs.zavar.commands.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutionException;

import itmo.labs.zavar.db.DataBaseManager;
import itmo.labs.zavar.db.DbUtils;
import itmo.labs.zavar.studygroup.Coordinates;
import itmo.labs.zavar.studygroup.FormOfEducation;
import itmo.labs.zavar.studygroup.Person;
import itmo.labs.zavar.studygroup.StudyGroup;

/**
 * This class contains main information: commands' map, collection, command's
 * history, init time.
 * 
 * @author Zavar
 * @version 1.4
 */

public class Environment {
	private HashMap<String, Command> map;
	private History history;
	private Stack<StudyGroup> stack;
	private DataBaseManager db;

	/**
	 * Creates new server environment for commands. Collection's creation date will
	 * be equals to file's creation date. If it won't be able to get file's
	 * attributes, collection's creation date will be set to the current.
	 * 
	 * @param file  File with collection.
	 * @param map   Commands' map.
	 * @param stack Main collection.
	 */
	public Environment(DataBaseManager db, HashMap<String, Command> map, Stack<StudyGroup> stack) {
		this.map = map;
		this.stack = stack;
		history = new History();
		this.db = db;
	}

	public void updateCollection() {
		ResultSet rs = null;
		try {
			rs = db.readFromDB(DbUtils.loadAll()).get();
			while (rs.next()) {
				long id = rs.getLong("id");
				String name = rs.getString("name");
				Coordinates coordinates = new Coordinates(rs.getDouble("x"), rs.getFloat("y"));
				Long studentsCount = rs.getLong("studentsCount");
				int expelledStudents = rs.getInt("expelledStudents");
				long transferredStudents = rs.getLong("transferredStudents");
				FormOfEducation formOfEducation = FormOfEducation.valueOf(rs.getString("formOfEducation"));
				Person groupAdmin = null;

				/*String admName = "";
				String passportID = "";
				Color eyeColor = null;
				Color hairColor = null;
				Country nationality = null;
				Location location;
				String nameStr = "";
				float x1 = 0f;
				Float y1 = 0f;
				Long z = 0l;*/
				
				stack.add(new StudyGroup(id, name, coordinates, studentsCount, expelledStudents, transferredStudents, formOfEducation, groupAdmin));
				
			}
			rs.close();
		} catch (InterruptedException | ExecutionException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public DataBaseManager getDbManager() {
		return db;
	}

	/**
	 * Creates new client environment for commands.
	 * 
	 * @param map Commands' map.
	 */
	public Environment(HashMap<String, Command> map) {
		history = new History();
		this.map = map;
	}

	/**
	 * Returns commands' map.
	 * 
	 * @return {@link HashMap}
	 */
	public HashMap<String, Command> getCommandsMap() {
		return map;
	}

	/**
	 * Returns main collection.
	 * 
	 * @return {@link Stack}
	 */
	public Stack<StudyGroup> getCollection() {
		return stack;
	}

	/**
	 * Returns history.
	 * 
	 * @return {@link History}
	 */
	public History getHistory() {
		return history;
	}

	/**
	 * Class uses to contain global history of commands and to contain temp history
	 * of "execute_script" command to prevent recursion.
	 * 
	 * @author Zavar
	 * @version 1.0
	 */
	public class History {
		private Stack<String> globalHistory = new Stack<String>();
		private Stack<String> tempHistory = new Stack<String>();

		/**
		 * Clears temp history.
		 */
		public void clearTempHistory() {
			tempHistory.clear();
		}

		/**
		 * Returns global command history.
		 * 
		 * @return {@link Stack}
		 */
		public Stack<String> getGlobalHistory() {
			return globalHistory;
		}

		/**
		 * Returns temp command history.
		 * 
		 * @return {@link Stack}
		 */
		public Stack<String> getTempHistory() {
			return tempHistory;
		}

		/**
		 * Adds command to global history.
		 * 
		 * @param to Command to add.
		 */
		public void addToGlobal(String to) {
			globalHistory.push(to);
		}

		/**
		 * Adds command to temp history.
		 * 
		 * @param to Command to add.
		 */
		public void addToTemp(String to) {
			tempHistory.push(to);
		}
	}
}
