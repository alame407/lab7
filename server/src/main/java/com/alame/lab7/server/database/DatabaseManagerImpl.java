package com.alame.lab7.server.database;

import com.alame.lab7.common.data.*;
import com.alame.lab7.server.utils.EnumUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class DatabaseManagerImpl implements DatabaseManager {
	private final DatabaseConnectionManager databaseConnectionManager;

	public DatabaseManagerImpl(DatabaseConnectionManager databaseConnectionManager) throws SQLException{
		this.databaseConnectionManager = databaseConnectionManager;
		createTablesIfNotExists();
	}
	@Override
	public void createTablesIfNotExists() throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try(connection) {
			connection.setAutoCommit(false);
			createEnums(connection);
			createCoordinates(connection);
			createPersons(connection);
			createUsers(connection);
			createPersons(connection);
			createStudyGroups(connection);
			createElements(connection);
			connection.commit();
			connection.setAutoCommit(true);
		}
	}
	private void createEnums(Connection connection) throws SQLException {
		String sqlCheckCountyExist = """
				DO $$
				BEGIN
				    create type country as enum ('RUSSIA', 'VATICAN', 'SOUTH_KOREA');
				EXCEPTION
				    WHEN duplicate_object THEN null;
				END $$;
				DO $$
				BEGIN
				    create type eyes_color as enum ('RED', 'BLUE', 'BROWN');
				EXCEPTION
				    WHEN duplicate_object THEN null;
				END $$;
				DO $$
				BEGIN
				    create type form_of_education as enum ('DISTANCE_EDUCATION', 'FULL_TIME_EDUCATION', 'EVENING_CLASSES');
				EXCEPTION
				    WHEN duplicate_object THEN null;
				END $$;
				DO $$
				BEGIN
				    create type hair_color as enum ('GREEN', 'RED', 'ORANGE', 'WHITE');
				EXCEPTION
				    WHEN duplicate_object THEN null;
				END $$;
				DO $$
				BEGIN
				    create type semester as enum ('FIRST', 'SIXTH', 'SEVENTH', 'EIGHTH');
				EXCEPTION
				    WHEN duplicate_object THEN null;
				END $$;""";
		PreparedStatement checkCountyExist = connection.prepareStatement(sqlCheckCountyExist);
		checkCountyExist.executeUpdate();

	}
	private void createCoordinates(Connection connection) throws SQLException{
		String sqlCreateCoordinates = """
				create table IF NOT EXISTS coordinates
				(
				    id serial
				        primary key,
				    x  bigint NOT NULL,
				    y  float NOT NULL
				);""";
		PreparedStatement createCoordinates = connection.prepareStatement(sqlCreateCoordinates);
		createCoordinates.executeUpdate();
	}
	private void createPersons(Connection connection) throws SQLException{
		String sqlCreatePersons = """
				create table IF NOT EXISTS persons
				(
				    id          serial
				        primary key,
				    name        text NOT NULL ,
				    birthday    date NOT NULL ,
				    eyes_color   eyes_color NOT NULL ,
				    hair_color   hair_color,
				    nationality country NOT NULL
				);""";
		PreparedStatement createPersons = connection.prepareStatement(sqlCreatePersons);
		createPersons.executeUpdate();
	}
	private void createUsers(Connection connection) throws SQLException{
		String sqlCreateUsers = """
				create table IF NOT EXISTS users
				(
				    id       serial
				        primary key,
				    username varchar(40) not null
				        unique
				        constraint users_username_check
				            check (length((username)::text) > 3),
				    password varchar(40) not null
				        constraint users_password_check
				            check (length((password)::text) > 3),
				    salty    varchar(10) NOT NULL
				);""";
		PreparedStatement createUsers = connection.prepareStatement(sqlCreateUsers);
		createUsers.executeUpdate();
	}
	private void createStudyGroups(Connection connection) throws SQLException{
		String sqlCreateStudyGroups = """
				create table IF NOT EXISTS study_groups
				(
				    id               serial
				        primary key,
				    name             text NOT NULL ,
				    coordinates_id   integer not null
				        unique
				        references coordinates,
				    creation_date     date NOT NULL ,
				    students_count    integer NOT NULL CHECK ( students_count>0 ),
				    expelled_students bigint NOT NULL check ( expelled_students>0 ),
				    form_of_education  form_of_education,
				    semester_enum     semester,
				    group_admin_id    integer not null
				        unique
				        references persons,
				    creator_id       integer not null
				        references users
				);""";
		PreparedStatement createStudyGroups = connection.prepareStatement(sqlCreateStudyGroups);
		createStudyGroups.executeUpdate();
	}
	private void createElements(Connection connection) throws SQLException{
		String sqlCreateElements = """
				create table IF NOT EXISTS elements
				(
				    id           serial
				        primary key,
				    key          text    not null
				        unique,
				    study_group_id integer not null
				        unique
				        references study_groups
				);""";
		PreparedStatement createElements = connection.prepareStatement(sqlCreateElements);
		createElements.executeUpdate();
	}
	@Override
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
		PreparedStatement insertElement = connection.prepareStatement("INSERT INTO elements(key, study_group_id)" +
				" VALUES (?,?)");
		insertElement.setString(1, key);
		insertElement.setInt(2, studyGroupId);
		insertElement.executeUpdate();
		return studyGroupId;
	}
	private int insertStudyGroup(StudyGroup studyGroup, Connection connection) throws SQLException {
		int coordinatesId = insertCoordinates(studyGroup.getCoordinates(), connection);
		int personId = insertPerson(studyGroup.getGroupAdmin(), connection);
		String sqlInsertStudyGroup = "INSERT INTO study_groups(name, coordinates_id, creation_date, students_count, " +
				"expelled_students, form_of_education, semester_enum, group_admin_id, creator_id) " +
				"VALUES (?, ?, ?, ?, ?, ?::form_of_education, ?::semester, ?, ?) RETURNING id";
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
		String sqlInsertPerson = "INSERT INTO persons(name, birthday, eyes_color, hair_color, nationality) " +
				"VALUES (?, ?, ?::eyes_color, ?::hair_color, ?::country) RETURNING id";
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
	@Override
	public boolean updateStudyGroup(int id, String name, Coordinates coordinates, int studentsCount, long expelledStudents,
	                                FormOfEducation formOfEducation, Semester semester,
	                                Person person) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			connection.setAutoCommit(false);
			String sqlGetIdes = "SELECT coordinates_id, group_admin_id FROM study_groups WHERE id=?";
			PreparedStatement getIdes = connection.prepareStatement(sqlGetIdes);
			getIdes.setInt(1, id);
			ResultSet ides = getIdes.executeQuery();
			ides.next();
			int coordinatesId = ides.getInt("coordinates_id");
			int personId = ides.getInt("group_admin_id");
			String sqlUpdateCoordinates = "UPDATE coordinates set x=?, y=? WHERE id=?";
			PreparedStatement updateCoordinates = connection.prepareStatement(sqlUpdateCoordinates);
			updateCoordinates.setLong(1, coordinates.getX());
			updateCoordinates.setFloat(2, coordinates.getY());
			updateCoordinates.setInt(3, coordinatesId);
			updateCoordinates.executeUpdate();
			String sqlUpdatePerson = "UPDATE persons set name=?, birthday=?, eyes_color=?::eyes_color, " +
					"hair_color=?::hair_color," +
					" nationality=?::country WHERE id=?";
			PreparedStatement updatePerson = connection.prepareStatement(sqlUpdatePerson);
			updatePerson.setString(1, person.getName());
			updatePerson.setDate(2, Date.valueOf(person.getBirthday()));
			updatePerson.setString(3, EnumUtils.name(person.getEyeColor()));
			updatePerson.setString(4, EnumUtils.name(person.getHairColor()));
			updatePerson.setString(5, EnumUtils.name(person.getNationality()));
			updatePerson.setInt(6, personId);
			updatePerson.executeUpdate();
			String sqlUpdateStudyGroup = "UPDATE study_groups set name=?, students_count=?, expelled_students=?, " +
					"form_of_education=?::form_of_education, semester_enum=?::semester WHERE id=?";
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
	@Override
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
		String sqlDeleteStudyGroup = "DELETE FROM study_groups WHERE id=? RETURNING coordinates_id, group_admin_id";
		PreparedStatement deleteStudyGroup = connection.prepareStatement(sqlDeleteStudyGroup);
		deleteStudyGroup.setInt(1, studyGroupId);
		ResultSet ides = deleteStudyGroup.executeQuery();
		ides.next();
		int coordinatesId = ides.getInt("coordinates_id");
		int personId = ides.getInt("group_admin_id");
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
	@Override
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
		String sqlDeleteElement = "DELETE FROM elements WHERE study_group_id=?";
		PreparedStatement deleteElement = connection.prepareStatement(sqlDeleteElement);
		deleteElement.setInt(1, studyGroupId);
		deleteElement.executeUpdate();
		deleteStudyGroup(studyGroupId,connection);
	}
	private void deleteElement(String key, Connection connection) throws SQLException{
		String sqlDeleteElement = "DELETE FROM elements WHERE key=? RETURNING study_group_id";
		PreparedStatement deleteElement = connection.prepareStatement(sqlDeleteElement);
		deleteElement.setString(1, key);
		ResultSet resultSet = deleteElement.executeQuery();
		resultSet.next();
		int studyGroupId = resultSet.getInt("study_group_id");
		deleteStudyGroup(studyGroupId, connection);
	}
	@Override
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
	@Override
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
	@Override
	public boolean checkAccess(String username, int studyGroupId) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try(connection) {
			String sqlGetUsername = "SELECT username FROM(SELECT creator_id FROM study_groups WHERE id=?) " +
					"AS study_group_id JOIN users ON study_group_id.creator_id=users.id";
			PreparedStatement getUsername = connection.prepareStatement(sqlGetUsername);
			getUsername.setInt(1, studyGroupId);
			ResultSet resultUserName = getUsername.executeQuery();
			if(!resultUserName.next()) return false;
			return username.equals(resultUserName.getString("username"));
		}
	}
	@Override
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
	@Override
	public List<String> getUserKeys(String username) throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		try (connection){
			String sqlGetUsersKeys = "SELECT elements.key FROM users join study_groups on users.username=? " +
					"AND users.id = study_groups.creator_id join elements on elements.study_group_id=study_groups.id";
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
	@Override
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
	@Override
	public Map<String, StudyGroup> load() throws SQLException{
		Connection connection = databaseConnectionManager.getConnection();
		Map<String, StudyGroup> result = new HashMap<>();
		try(connection) {
			String sqlGetAllFields = "SELECT e.key, e.study_group_id, s.name, " +
					"s.creation_date, s.students_count, s.expelled_students, s.form_of_education, s.semester_enum, " +
					"creator_id, c.x, c.y, p.name as person_name, p.birthday, p.eyes_color, p.hair_color, p.nationality " +
					"FROM elements e join study_groups s on s.id = e.study_group_id " +
					"join coordinates c on s.coordinates_id = c.id join persons p on p.id = s.group_admin_id";
			PreparedStatement getAllFields = connection.prepareStatement(sqlGetAllFields);
			ResultSet allFields = getAllFields.executeQuery();

			while(allFields.next()){
				String personName = allFields.getString("person_name");
				LocalDate birthday = allFields.getDate("birthday").toLocalDate();
				EyesColor eyesColor = EyesColor.valueOf(allFields.getString("eyes_color"));
				String stringHairColor = allFields.getString("hair_color");
				HairColor hairColor = stringHairColor==null? null:HairColor.valueOf(stringHairColor);
				Country nationality = Country.valueOf(allFields.getString("nationality"));
				Person person = new Person(personName, birthday, eyesColor, hairColor, nationality);
				long x = allFields.getLong("x");
				float y = allFields.getFloat("y");
				Coordinates coordinates = new Coordinates(x,y);
				int id = allFields.getInt("study_group_id");
				String name = allFields.getString("name");
				LocalDate creationDate = allFields.getDate("creation_date").toLocalDate();
				int studentsCount = allFields.getInt("students_count");
				long expelledStudents = allFields.getLong("expelled_students");
				String stringFormOfEducation = allFields.getString("form_of_education");
				FormOfEducation form = stringFormOfEducation==null? null:FormOfEducation.valueOf(stringFormOfEducation);
				String stringSemester  =allFields.getString("semester_enum");
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
	@Override
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
	@Override
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
