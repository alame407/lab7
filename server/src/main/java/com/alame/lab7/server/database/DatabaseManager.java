package com.alame.lab7.server.database;

import com.alame.lab7.common.data.*;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseManager {
	void createTablesIfNotExists() throws SQLException;

	int insertElement(String key, StudyGroup studyGroup)
			throws SQLException;

	boolean updateStudyGroup(int id, String name, Coordinates coordinates, int studentsCount, long expelledStudents,
	                         FormOfEducation formOfEducation, Semester semester,
	                         Person person) throws SQLException;

	boolean deleteElement(String key) throws SQLException;

	boolean deleteElement(int studyGroupId) throws SQLException;

	boolean deleteElements(List<String> keys) throws SQLException;

	void insertUser(String userName, String password, String salty) throws SQLException;

	boolean checkAccess(String username, int studyGroupId) throws SQLException;

	int getUserId(String username) throws SQLException;

	List<String> getUserKeys(String username) throws SQLException;

	boolean replace(String key, StudyGroup newStudyGroup) throws SQLException;

	Map<String, StudyGroup> load() throws SQLException;

	Pair<String, String> getPasswordAndSalty(String username) throws SQLException;

	boolean usernameExist(String username) throws SQLException;
}
