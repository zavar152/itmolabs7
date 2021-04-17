package itmo.labs.zavar.db;

public class DbUtils {

	public static String getAll() {
		return "SELECT * FROM studygroups;";
	}

	public static String getCount() {
		return "SELECT COUNT(*) FROM studygroups;";
	}

	public static String countGreaterThanTs(long ts) {
		return "SELECT COUNT(transferredstudents) FROM studygroups WHERE transferredstudents > " + ts + ";";
	}
	
	public static String getCreationDate() {
		return "SELECT MIN(creationdate) FROM studygroups;";
	}
	
	public static String clearAll() {
		return "TRUNCATE TABLE studygroups;";
	}
	
	public static String deleteById(int id) {
		return "DELETE FROM studygroups WHERE id = " + id + ";";
	}
	
	public static String deleteBySc(long sc) {
		return "DELETE FROM studygroups WHERE studentsCount = " + sc + ";";
	}
	
	public static String averageOfTs() {
		return "SELECT AVG(transferredStudents) FROM studygroups;";
	}
	
	public static String deleteMainTable() {
		return "DROP TABLE studygroups;";
	}
	
	public static String createMainTable() {
		return "create table studygroups (\r\n"
				+ "    id BIGINT PRIMARY KEY NOT NULL UNIQUE CHECK ( id > 0 ),\r\n"
				+ "    name text NOT NULL,\r\n"
				+ "    x real NOT NULL CHECK ( x > -573 ),\r\n"
				+ "    y double precision NOT NULL,\r\n"
				+ "    creationDate date NOT NULL,\r\n"
				+ "    studentsCount bigint NOT NULL CHECK ( studentsCount > 0 ),\r\n"
				+ "    expelledStudents int NOT NULL CHECK ( expelledStudents > 0 ),\r\n"
				+ "    transferredStudents bigint NOT NULL CHECK ( transferredStudents > 0 ),\r\n"
				+ "    formOfEducation text NOT NULL CHECK ( formOfEducation IN ('DISTANCE_EDUCATION', 'FULL_TIME_EDUCATION', 'EVENING_CLASSES')),\r\n"
				+ "    adminName text,\r\n"
				+ "    adminPassportID text,\r\n"
				+ "    adminEyeColor text NOT NULL CHECK ( adminEyeColor IN ('GREEN', 'BLACK', 'BLUE', 'WHITE') ),\r\n"
				+ "    adminHairColor text NOT NULL CHECK ( adminHairColor IN ('GREEN', 'BLACK', 'BLUE', 'WHITE') ),\r\n"
				+ "    adminNationality text CHECK ( adminNationality IN ('USA', 'GERMANY', 'INDIA', 'VATICAN', 'ITALY') ),\r\n"
				+ "    adminLocationX real NOT NULL,\r\n"
				+ "    adminLocationY real NOT NULL,\r\n"
				+ "    adminLocationZ double precision NOT NULL,\r\n"
				+ "    adminLocationName text NOT NULL CHECK ( length(adminLocationName) < 348 )\r\n"
				+ ");";
	}
	
	public static String addSequence() {
		return "create sequence sequence_id\r\n"
				+ "MINVALUE 1\r\n"
				+ "START WITH 1\r\n"
				+ "INCREMENT BY 1\r\n"
				+ "CACHE 20;";
	}
}
