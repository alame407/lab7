package com.alame.lab7.server.database;

import com.alame.lab7.common.data.*;
import com.alame.lab7.server.utils.EnumUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DatabaseManager {
	private final DatabaseConnectionManager databaseConnectionManager;

	public DatabaseManager(DatabaseConnectionManager databaseConnectionManager) {
		this.databaseConnectionManager = databaseConnectionManager;
	}
	public int insertElement(String key, StudyGroup studyGroup)
			throws SQLException {
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			connection.setAutoCommit(false);
			int studyGroupId = insertElement(key, studyGroup, connection);
			connection.commit();
			connection.setAutoCommit(true);
			return studyGroupId;
		}
	}
	private int insertElement(String key, StudyGroup studyGroup, Connection connection) throws SQLException {
		int studyGroupId = insertStudyGroup(studyGroup, connection);
		PreparedStatement insertElement = connection.prepareStatement("INSERT INTO elements(key, studygroupid)" +
				" VALUES (?,?)");
		insertElement.setString(1, key);
		insertElement.setInt(2, studyGroupId);
		insertElement.executeUpdate();
		return studyGroupId;
	}
	private int insertStudyGroup(StudyGroup studyGroup, Connection connection) throws SQLException {
		int coordinatesId = insertCoordinates(studyGroup.getCoordinates(), connection);
		int personId = insertPerson(studyGroup.getGroupAdmin(), connection);
		String sqlInsertStudyGroup = "INSERT INTO studygroups(name, coordinates_id, creationdate, studentscount, " +
				"expelledstudents, formofeducation, semesterenum, groupadmin_id, creator_id) " +
				"VALUES (?, ?, ?, ?, ?, ?::FormOfEducation, ?::Semester, ?, ?) RETURNING id";
		PreparedStatement insertStudyGroup = connection.prepareStatement(sqlInsertStudyGroup);
		insertStudyGroup.setString(1, studyGroup.getName());
		insertStudyGroup.setInt(2, coordinatesId);
		insertStudyGroup.setDate(3, Date.valueOf(studyGroup.getCreationDate()));
		insertStudyGroup.setInt(4, studyGroup.getStudentsCount());
		insertStudyGroup.setLong(5, studyGroup.getExpelledStudents());
		insertStudyGroup.setString(6, EnumUtils.name(studyGroup.getFormOfEducation()));
		insertStudyGroup.setString(7, EnumUtils.name(studyGroup.getSemesterEnum()));
		insertStudyGroup.setInt(8, personId);
		insertStudyGroup.setInt(9, studyGroup.getCreatorId());
		ResultSet resultIdStudyGroup = insertStudyGroup.executeQuery();
		resultIdStudyGroup.next();
		return resultIdStudyGroup.getInt("id");
	}
	private int insertCoordinates(Coordinates coordinates, Connection connection) throws SQLException {
		String sqlInsertCoordinates = "INSERT INTO coordinates(x,y) VALUES (?, ?) RETURNING id";
		PreparedStatement insertCoordinates = connection.prepareStatement(sqlInsertCoordinates);
		insertCoordinates.setLong(1, coordinates.getX());
		insertCoordinates.setFloat(2, coordinates.getY());
		ResultSet resultIdCoordinates = insertCoordinates.executeQuery();
		resultIdCoordinates.next();
		return resultIdCoordinates.getInt("id");
	}
	private int insertPerson(Person person, Connection connection) throws SQLException {
		String sqlInsertPerson = "INSERT INTO persons(name, birthday, eyescolor, haircolor, nationality) " +
				"VALUES (?, ?, ?::EyesColor, ?::HairColor, ?::Country) RETURNING id";
		PreparedStatement insertPerson = connection.prepareStatement(sqlInsertPerson);
		insertPerson.setString(1, person.getName());
		insertPerson.setDate(2, Date.valueOf(person.getBirthday()));
		insertPerson.setString(3, person.getEyeColor().name());
		insertPerson.setString(4, EnumUtils.name(person.getHairColor()));
		insertPerson.setString(5, person.getNationality().name());
		ResultSet resultIdPerson = insertPerson.executeQuery();
		resultIdPerson.next();
		return resultIdPerson.getInt("id");
	}
	public boolean updateStudyGroup(int id, String name, Coordinates coordinates, int studentsCount, long expelledStudents,
	                                FormOfEducation formOfEducation, Semester semester,
	                                Person person) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			connection.setAutoCommit(false);
			String sqlGetIdes = "SELECT coordinates_id, groupadmin_id FROM studygroups WHERE id=?";
			PreparedStatement getIdes = connection.prepareStatement(sqlGetIdes);
			getIdes.setInt(1, id);
			ResultSet ides = getIdes.executeQuery();
			ides.next();
			int coordinatesId = ides.getInt("coordinates_id");
			int personId = ides.getInt("groupAdmin_id");
			String sqlUpdateCoordinates = "UPDATE coordinates set x=?, y=? WHERE id=?";
			PreparedStatement updateCoordinates = connection.prepareStatement(sqlUpdateCoordinates);
			updateCoordinates.setLong(1, coordinates.getX());
			updateCoordinates.setFloat(2, coordinates.getY());
			updateCoordinates.setInt(3, coordinatesId);
			updateCoordinates.executeUpdate();
			String sqlUpdatePerson = "UPDATE persons set name=?, birthday=?, eyescolor=?::EyesColor, haircolor=?::HairColor," +
					" nationality=?::Country WHERE id=?";
			PreparedStatement updatePerson = connection.prepareStatement(sqlUpdatePerson);
			updatePerson.setString(1, person.getName());
			updatePerson.setDate(2, Date.valueOf(person.getBirthday()));
			updatePerson.setString(3, EnumUtils.name(person.getEyeColor()));
			updatePerson.setString(4, EnumUtils.name(person.getHairColor()));
			updatePerson.setString(5, EnumUtils.name(person.getNationality()));
			updatePerson.setInt(6, personId);
			updatePerson.executeUpdate();
			String sqlUpdateStudyGroup = "UPDATE studygroups set name=?, studentscount=?, expelledstudents=?, " +
					"formofeducation=?::FormOfEducation, semesterenum=?::Semester WHERE id=?";
			PreparedStatement updateStudyGroup = connection.prepareStatement(sqlUpdateStudyGroup);
			updateStudyGroup.setString(1, name);
			updateStudyGroup.setInt(2, studentsCount);
			updateStudyGroup.setLong(3, expelledStudents);
			updateStudyGroup.setString(4, EnumUtils.name(formOfEducation));
			updateStudyGroup.setString(5, EnumUtils.name(semester));
			updateStudyGroup.setInt(6, id);
			updateStudyGroup.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			return true;
		}
	}
	public boolean deleteElement(String key) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			connection.setAutoCommit(false);
			deleteElement(key, connection);
			connection.commit();
			connection.setAutoCommit(true);
			return true;
		}
	}
	private void deleteStudyGroup(int studyGroupId, Connection connection) throws SQLException{
		String sqlDeleteStudyGroup = "DELETE FROM studygroups WHERE id=? RETURNING coordinates_id, groupadmin_id";
		PreparedStatement deleteStudyGroup = connection.prepareStatement(sqlDeleteStudyGroup);
		deleteStudyGroup.setInt(1, studyGroupId);
		ResultSet ides = deleteStudyGroup.executeQuery();
		ides.next();
		int coordinatesId = ides.getInt("coordinates_id");
		int personId = ides.getInt("groupAdmin_id");
		deleteCoordinates(coordinatesId, connection);
		deletePerson(personId, connection);
	}
	private void deleteCoordinates(int coordinatesId, Connection connection)throws SQLException{
		String sqlDeleteCoordinates = "DELETE FROM coordinates WHERE id=?";
		PreparedStatement deleteCoordinates = connection.prepareStatement(sqlDeleteCoordinates);
		deleteCoordinates.setInt(1, coordinatesId);
		deleteCoordinates.executeUpdate();
	}
	private void deletePerson(int personId, Connection connection)throws SQLException{
		String sqlDeletePerson = "DELETE FROM persons WHERE id=?";
		PreparedStatement deletePerson = connection.prepareStatement(sqlDeletePerson);
		deletePerson.setInt(1, personId);
		deletePerson.executeUpdate();
	}
	public boolean deleteElement(int studyGroupId)throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			connection.setAutoCommit(false);
			deleteElement(studyGroupId, connection);
			connection.commit();
			connection.setAutoCommit(true);
			return true;
		}
	}
	private void deleteElement(int studyGroupId, Connection connection) throws SQLException{
		String sqlDeleteElement = "DELETE FROM elements WHERE studygroupid=?";
		PreparedStatement deleteElement = connection.prepareStatement(sqlDeleteElement);
		deleteElement.setInt(1, studyGroupId);
		deleteElement.executeUpdate();
		deleteStudyGroup(studyGroupId,connection);
	}
	private void deleteElement(String key, Connection connection) throws SQLException{
		String sqlDeleteElement = "DELETE FROM elements WHERE key=? RETURNING studygroupid";
		PreparedStatement deleteElement = connection.prepareStatement(sqlDeleteElement);
		deleteElement.setString(1, key);
		ResultSet resultSet = deleteElement.executeQuery();
		resultSet.next();
		int studyGroupId = resultSet.getInt("studyGroupId");
		deleteStudyGroup(studyGroupId, connection);
	}
	public boolean deleteElements(List<String> keys) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try(connection) {
			connection.setAutoCommit(false);
			for (String key:keys) {
				deleteElement(key, connection);
			}
			connection.commit();
			connection.setAutoCommit(true);
			return true;
		}
	}
	public void insertUser(String userName, String password, String salty) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			String sqlInsertUser = "INSERT INTO users(username, password, salty) VALUES (?,?,?)";
			PreparedStatement insertUser = connection.prepareStatement(sqlInsertUser);
			insertUser.setString(1,userName);
			insertUser.setString(2, password);
			insertUser.setString(3, salty);
			insertUser.executeUpdate();
		}
	}
	public boolean checkAccess(String username, int studyGroupId) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try(connection) {
			String sqlGetUsername = "SELECT username FROM(SELECT creator_id FROM studygroups WHERE id=?) " +
					"AS studyGtoupId JOIN users ON studyGtoupId.creator_id=users.id";
			PreparedStatement getUsername = connection.prepareStatement(sqlGetUsername);
			getUsername.setInt(1, studyGroupId);
			ResultSet resultUserName = getUsername.executeQuery();
			resultUserName.next();
			return username.equals(resultUserName.getString("username"));
		}
	}
	public int getUserId(String username) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try(connection){
			String sqlGetSalty = "SELECT id FROM users where username=?";
			PreparedStatement getSalty = connection.prepareStatement(sqlGetSalty);
			getSalty.setString(1, username);
			ResultSet resultSalty = getSalty.executeQuery();
			resultSalty.next();
			return resultSalty.getInt("id");
		}
	}
	public List<String> getUserKeys(String username) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			String sqlGetUsersKeys = "SELECT elements.key FROM users join studygroups on users.username=? " +
					"AND users.id = studygroups.creator_id join elements on elements.studygroupid=studygroups.id";
			PreparedStatement getUsersKeys = connection.prepareStatement(sqlGetUsersKeys);
			getUsersKeys.setString(1, username);
			ResultSet resultKeys = getUsersKeys.executeQuery();
			List<String> result = new ArrayList<>();
			while(resultKeys.next()){
				result.add(resultKeys.getString("key"));
			}
			return result;
		}
	}
	public boolean replace(String key, StudyGroup newStudyGroup) throws SQLException {
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			connection.setAutoCommit(false);
			deleteElement(key, connection);
			insertElement(key, newStudyGroup, connection);
			connection.commit();
			connection.setAutoCommit(true);
			return true;
		}
	}
	public Map<String, StudyGroup> load() throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		Map<String, StudyGroup> result = new HashMap<>();
		try(connection) {
			String sqlGetAllFields = "SELECT e.key, e.studygroupid, s.name, " +
					"s.creationdate, s.studentscount, s.expelledstudents, s.formofeducation, s.semesterenum, " +
					"creator_id, c.x, c.y, p.name as personName, p.birthday, p.eyescolor, p.haircolor, p.nationality " +
					"FROM elements e join studygroups s on s.id = e.studygroupid " +
					"join coordinates c on s.coordinates_id = c.id join persons p on p.id = s.groupadmin_id";
			PreparedStatement getAllFields = connection.prepareStatement(sqlGetAllFields);
			ResultSet allFields = getAllFields.executeQuery();

			while(allFields.next()){
				String personName = allFields.getString("personName");
				LocalDate birthday = allFields.getDate("birthday").toLocalDate();
				EyesColor eyesColor = EyesColor.valueOf(allFields.getString("eyescolor"));
				String stringHairColor = allFields.getString("hairColor");
				HairColor hairColor = stringHairColor==null? null:HairColor.valueOf(stringHairColor);
				Country nationality = Country.valueOf(allFields.getString("nationality"));
				Person person = new Person(personName, birthday, eyesColor, hairColor, nationality);
				long x = allFields.getLong("x");
				float y = allFields.getFloat("y");
				Coordinates coordinates = new Coordinates(x,y);
				int id = allFields.getInt("studygroupid");
				String name = allFields.getString("name");
				LocalDate creationDate = allFields.getDate("creationdate").toLocalDate();
				int studentsCount = allFields.getInt("studentscount");
				long expelledStudents = allFields.getLong("expelledstudents");
				String stringFormOfEducation = allFields.getString("formofeducation");
				FormOfEducation form = stringFormOfEducation==null? null:FormOfEducation.valueOf(stringFormOfEducation);
				String stringSemester  =allFields.getString("semesterenum");
				Semester semester = stringSemester==null?null:Semester.valueOf(stringSemester);
				int creator_id = allFields.getInt("creator_id");
				String key = allFields.getString("key");
				StudyGroup studyGroup = new StudyGroup(id, name, coordinates, creationDate, studentsCount,
						expelledStudents, form, semester, person, creator_id);
				result.put(key, studyGroup);
			}
		}
		return result;
	}
	public Pair<String, String> getPasswordAndSalty(String username) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try(connection){
			String sqlGetSaltyAndPassword = "SELECT password, salty FROM users where username=?";
			PreparedStatement getSaltyAndPassword = connection.prepareStatement(sqlGetSaltyAndPassword);
			getSaltyAndPassword.setString(1, username);
			ResultSet resultSaltyPassword = getSaltyAndPassword.executeQuery();
			resultSaltyPassword.next();
			String salty = resultSaltyPassword.getString("salty");
			String password = resultSaltyPassword.getString("password");
			return new ImmutablePair<>(password, salty);
		}
	}
	public boolean usernameExist(String username) throws SQLException {
		Connection connection = databaseConnectionManager.getConnection();
		try(connection){
			String sqlUserExist = "Select exists(SELECT username FROM users WHERE username=?)";
			PreparedStatement UserExists = connection.prepareStatement(sqlUserExist);
			UserExists.setString(1, username);
			ResultSet resultExists = UserExists.executeQuery();
			resultExists.next();
			return resultExists.getBoolean("exists");
		}
	}
}
