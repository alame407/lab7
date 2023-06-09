package com.alame.lab7.common.request;


import com.alame.lab7.common.data.*;
import com.alame.lab7.common.exceptions.*;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;
import java.util.List;

/**
 * interface server for a client project
 */
public interface ClientServerInterface {
    /**
     * info about collection
     * @return info
     */
    String info(User user) throws NoSuchUserException, SQLException;

    /**
     * get all values of collection
     * @return all values
     */
    List<StudyGroup> getAllValues(User user) throws NoSuchUserException, SQLException;

    /**
     * insert pair key, element in collection
     * @param key - key to insert
     * @param element - element to insert
     * @throws IncorrectElementFieldException if element is not valid
     */
    void insert(String key, StudyGroup element, User user) throws IncorrectElementFieldException, SQLException, NoSuchUserException, UserAccessException;
    /**
     * update fields of element
     * @param id - id of element
     * @param name - name of element
     * @param coordinates - coordinates of element
     * @param studentsCount - studentsCount of element
     * @param expelledStudents - expelledStudents of element
     * @param formOfEducation - formOfEducation of element
     * @param semester - semester of element
     * @param groupAdmin - groupAdmin of element
     * @throws IncorrectElementFieldException if fields are not valid
     */

    void update(int id, String name, Coordinates coordinates, int studentsCount, long expelledStudents,
                FormOfEducation formOfEducation, Semester semester, Person groupAdmin, User user)
		    throws IncorrectElementFieldException, UserAccessException, SQLException, NoSuchUserException, DatabaseUpdateException;
    /**
     * delete value by key
     * @param key - key in map
     */
    void removeKey(String key, User user) throws SQLException, UserAccessException, NoSuchUserException, DatabaseUpdateException;
    /**
     * clear collection
     */
    void clear(User user) throws SQLException, NoSuchUserException, DatabaseUpdateException;
    /**
     * delete all keys which less than iven
     * @param key - key for compare
     */
    void removeLowerKey(String key, User user) throws SQLException, NoSuchUserException, DatabaseUpdateException;
    /**
     * replace element by key if element less than given
     * @param key - key in map
     * @param newStudyGroup - new element value
     */
    void replaceIfLower(String key, StudyGroup newStudyGroup, User user)
            throws IncorrectElementFieldException, SQLException, UserAccessException, NoSuchUserException, DatabaseUpdateException;
    /**
     * delete all elements where formOfEducation equals to given
     * @param formOfEducation - formOfEducation to delete
     */
    void removeAllByFormOfEducation(FormOfEducation formOfEducation, User user)
            throws IncorrectElementFieldException, SQLException, NoSuchUserException, DatabaseUpdateException;
    /**
     * find max element by creationDate
     * @return max element by creation date
     * @throws CollectionIsEmptyException - if collection size=0
     */
    StudyGroup getMaxByCreationDate(User user) throws CollectionIsEmptyException, NoSuchUserException, SQLException;
    /**
     * get all group admins of study groups in reverse order
     * @return list of group admins
     */
    List<Person> getAllGroupAdminsInReverseOrder(User user) throws NoSuchUserException, SQLException;
    /**
     * check if id represented in collection
     * @param id - id of element
     * @return true if id represented in collection, else false
     */
    boolean idExist(int id, User user) throws NoSuchUserException, SQLException;
    /**
     * check if key represented in collection
     * @param key - key in map
     * @return true if key represented in collection, else false
     */
    boolean keyExist(String key, User user) throws SQLException, NoSuchUserException;
    void register(User user) throws UsernameAlreadyTakenException, SQLException,
            InvalidUsernameException, InvalidPasswordException;
    boolean signIn(User user) throws SQLException;
    boolean checkAccess(User user, int id) throws SQLException, NoSuchUserException, IdNotExistException;
    boolean checkAccess(User user, String key) throws SQLException, NoSuchUserException, KeyNotExistException;
}
