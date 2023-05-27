package com.alame.lab7.server.servers;

import com.alame.lab7.common.data.*;
import com.alame.lab7.common.exceptions.*;
import com.alame.lab7.common.user.User;
import com.alame.lab7.server.App;
import com.alame.lab7.server.database.DatabaseManager;
import org.apache.commons.lang3.tuple.Pair;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * class for collection control
 */
public class Server implements ServerInterface {
    /**
     * collection
     */
    private final ConcurrentMap<String, StudyGroup> studyGroupMap;
    private final static String pepper = "dsghes1we";
    /**
     * date the collection was created
     */
    private final java.time.LocalDate creationDate;
    /**
     * type of collection
     */
    private final String COLLECTION_TYPE = "TreeMap";
    private final Logger logger = App.logger;
    private final DatabaseManager databaseManager;
    private final StudyGroupValidator studyGroupValidator;
    private final Lock lock = new ReentrantLock();
    public Server(StudyGroupValidator studyGroupValidator, DatabaseManager databaseManager) {
        this.studyGroupValidator = studyGroupValidator;
        this.databaseManager = databaseManager;
        studyGroupMap = new ConcurrentSkipListMap<>();
        creationDate = java.time.LocalDate.now();
    }

    /**
     * @return info about collection
     */
    @Override
    public String info(User user)throws NoSuchUserException, SQLException{
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        return "тип коллекции: " + COLLECTION_TYPE + " дата создания коллекции: " + creationDate +
                " количество элементов: " + studyGroupMap.size();
    }

    /**
     * @return all studyGroups in collection
     */
    @Override
    public List<StudyGroup> getAllValues(User user) throws NoSuchUserException, SQLException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        return new ArrayList<>(studyGroupMap.values());
    }

    /**
     * insert a pair of key and element in collection
     * @param key - key in map
     * @param element - value in map
     */
    @Override
    public void insert(String key, StudyGroup element, User user) throws IncorrectElementFieldException, SQLException,
            NoSuchUserException, UserAccessException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        if(studyGroupMap.containsKey(key) && !databaseManager.checkAccess(user.getUsername(),
                studyGroupMap.get(key).getId()))
            throw new UserAccessException("пользователь " + user.getUsername() + " не имеет права на изменение объекта "
                    + "с ключом " + key);
        element.setId(1);
        element.setCreationDate(getCreationDate());
        if (!studyGroupValidator.validStudyGroup(element))
            throw new IncorrectElementFieldException("Поля элемента невалидные");
        int userId = databaseManager.getUserId(user.getUsername());
        element.setCreatorId(userId);
        lock.lock();
        try {
            int id = databaseManager.insertElement(key, element);
            element.setId(id);
            studyGroupMap.put(key, element);
            logger.info("новый элемент добавлен");
        }
        finally {
            lock.unlock();
        }
    }

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
    @Override
    public void update(int id, String name, Coordinates coordinates, int studentsCount, long expelledStudents,
                       FormOfEducation formOfEducation, Semester semester, Person groupAdmin, User user)
            throws IncorrectElementFieldException, SQLException, UserAccessException, NoSuchUserException, DatabaseUpdateException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        if (!(studyGroupValidator.validName(name) && studyGroupValidator.validCoordinates(coordinates) &&
                studyGroupValidator.validStudentsCount(studentsCount) &&
                studyGroupValidator.validExpelledStudents(expelledStudents) &&
                studyGroupValidator.validFormOfEducation(formOfEducation) && studyGroupValidator.validSemester(semester)&&
                 studyGroupValidator.validPerson(groupAdmin)))
            throw new IncorrectElementFieldException("поля элемента не валидны");
        for(StudyGroup studyGroup: studyGroupMap.values()){
            if (studyGroup.getId()==id) {
                if(!databaseManager.checkAccess(user.getUsername(), studyGroup.getId()))
                    throw new UserAccessException("у пользователя " + user.getUsername() + " нет прав на " +
                            "изменение элемента с id = "+id);
                lock.lock();
                try {

                    if (!databaseManager.updateStudyGroup(id, name, coordinates, studentsCount, expelledStudents,
                            formOfEducation, semester, groupAdmin)) {
                        throw new DatabaseUpdateException("не удалось обновить значение в базе данных");
                    }
                    studyGroup.setGroupAdmin(groupAdmin);
                    studyGroup.setName(name);
                    studyGroup.setCoordinates(coordinates);
                    studyGroup.setExpelledStudents(expelledStudents);
                    studyGroup.setFormOfEducation(formOfEducation);
                    studyGroup.setSemesterEnum(semester);
                    studyGroup.setStudentsCount(studentsCount);}
                finally {
                    lock.unlock();
                }
                    return;

            }

        }
    }

    /**
     * delete value by key
     * @param key - key in map
     */
    @Override
    public void removeKey(String key, User user) throws SQLException, UserAccessException, NoSuchUserException,
            DatabaseUpdateException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        if (studyGroupMap.containsKey(key)) {
            if(!databaseManager.checkAccess(user.getUsername(), studyGroupMap.get(key).getId()))
                throw new UserAccessException("у пользователя "+ user.getUsername() + " нет прав на удаление " +
                        "элемента с ключом: " +key);
            lock.lock();
            try {
                if (!databaseManager.deleteElement(key)) {
                    throw new DatabaseUpdateException("не удалось обновить базу данных");
                }
                studyGroupMap.remove(key);
                logger.info("элемент удален");
            }
            finally {
                lock.unlock();
            }
        }
    }

    /**
     * clear collection
     */
    @Override
    public void clear(User user) throws SQLException, NoSuchUserException, DatabaseUpdateException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
		List<String> keys = databaseManager.getUserKeys(user.getUsername());
        if(keys.size()==0) return;
        lock.lock();
        try {
            if (!databaseManager.deleteElements(keys)) {
                throw new DatabaseUpdateException("не удалось обновить базу данных");
            }
            for (String key : keys) {
                studyGroupMap.remove(key);
            }
            logger.info("все элементы пользователя " + user.getUsername() + " удалены");
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * delete all keys which less than iven
     * @param key - key for compare
     */
    @Override
    public void removeLowerKey(String key, User user) throws SQLException, NoSuchUserException, DatabaseUpdateException{
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        List<String> userKeys = databaseManager.getUserKeys(user.getUsername());
        List<String> keysToDelete = userKeys.stream().
                filter(keyElement -> keyElement.compareTo(key)<0).toList();
        if(keysToDelete.size()==0) return;
        lock.lock();
        try {
            if (!databaseManager.deleteElements(keysToDelete)) {
                throw new DatabaseUpdateException("не удалось обновить базу данных");
            }
            for (String keyToDelete : keysToDelete) {
                studyGroupMap.remove(keyToDelete);
            }
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * replace element by key if element less than given
     * @param key - key in map
     * @param newStudyGroup - new element value
     */
    @Override
    public void replaceIfLower(String key, StudyGroup newStudyGroup, User user)
            throws IncorrectElementFieldException, SQLException, UserAccessException, NoSuchUserException,
            DatabaseUpdateException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        newStudyGroup.setId(1);
        newStudyGroup.setCreationDate(getCreationDate());
        if (!studyGroupValidator.validStudyGroup(newStudyGroup))
            throw new IncorrectElementFieldException("элемент не валидный");
        if(studyGroupMap.containsKey(key)){
            StudyGroup studyGroup = studyGroupMap.get(key);
            if(!databaseManager.checkAccess(user.getUsername(), studyGroup.getId()))
                throw new UserAccessException("у пользователя " + user.getUsername() + " нет прав на изменение " +
                        "объекта с id "
                        +studyGroup.getId());
            if (newStudyGroup.compareTo(studyGroup) < 0){
                newStudyGroup.setCreatorId(databaseManager.getUserId(user.getUsername()));
                lock.lock();
                try {
                    if (!databaseManager.replace(key, newStudyGroup)) {
                        throw new DatabaseUpdateException("не удалось обновить базу данных");
                    }
                    studyGroupMap.replace(key, newStudyGroup);
                }
                finally {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * delete all elements where formOfEducation equals to given
     * @param formOfEducation - formOfEducation to delete
     */
    @Override
    public void removeAllByFormOfEducation(FormOfEducation formOfEducation, User user)
            throws IncorrectElementFieldException, SQLException, NoSuchUserException, DatabaseUpdateException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        if (!studyGroupValidator.validFormOfEducation(formOfEducation))
            throw new IncorrectElementFieldException("форма обучения не валидна");
        List<String> userKeys = databaseManager.getUserKeys(user.getUsername());
        List<String> keysToDelete = userKeys.stream().
                filter(s -> studyGroupMap.get(s).getFormOfEducation()==formOfEducation).toList();
        if(keysToDelete.size()==0) return;
        lock.lock();
        try {
            if (!databaseManager.deleteElements(keysToDelete)) {
                throw new DatabaseUpdateException("не удалось обновить базу данных");
            }
            for (String keyToDelete : keysToDelete) {
                studyGroupMap.remove(keyToDelete);
            }
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * find max element by creationDate
     * @return max element by creation date
     * @throws CollectionIsEmptyException - if collection size=0
     */
    @Override
    public StudyGroup getMaxByCreationDate(User user) throws CollectionIsEmptyException, NoSuchUserException,
            SQLException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        if (studyGroupMap.size()==0) throw new CollectionIsEmptyException("в коллекции нет элементов");
        return  Collections.max(studyGroupMap.values(), Comparator.comparing(StudyGroup::getCreationDate));
    }

    /**
     * get all group admins of study groups in reverse order
     * @return list of group admins
     */
    @Override
    public List<Person> getAllGroupAdminsInReverseOrder(User user) throws NoSuchUserException, SQLException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        return studyGroupMap.values().stream().map(StudyGroup::getGroupAdmin).sorted(Comparator.reverseOrder()).toList();
    }

    /**
     * put all values in map in collection
     * @param studyGroupMap - values to put
     */
    @Override
    public void putAll(Map<String, StudyGroup> studyGroupMap) throws IncorrectElementFieldException{
        for(StudyGroup studyGroup:studyGroupMap.values()){
            if (!studyGroupValidator.validStudyGroup(studyGroup) ||idExist(studyGroup.getId()))
                throw new IncorrectElementFieldException("поля элемента не валидны");
        }
        lock.lock();
        this.studyGroupMap.putAll(studyGroupMap);
        lock.unlock();
    }

    /**
     * check if id represented in collection
     * @param id - id of element
     * @return true if id represented in collection, else false
     */
    @Override
    public boolean idExist(int id, User user) throws NoSuchUserException, SQLException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        for (StudyGroup studyGroup: studyGroupMap.values()){
            if (studyGroup.getId()==id) return true;
        }
        return false;
    }

    /**
     * check if key represented in collection
     * @param key - key in map
     * @return true if key represented in collection, else false
     */
    @Override
    public boolean keyExist(String key, User user) throws SQLException, NoSuchUserException {
        if(!signIn(user)) throw new NoSuchUserException("неверное имя пользователя или пароль");
        return studyGroupMap.containsKey(key);
    }
    /**
     * generate creation date
     * @return current date
     */
    private LocalDate getCreationDate() {
        return LocalDate.now();
    }
    public boolean signIn(User user) throws SQLException{
        if(!databaseManager.usernameExist(user.getUsername())) return false;
        Pair<String, String> passwordAndSalty = databaseManager.getPasswordAndSalty(user.getUsername());
        String realPassword = passwordAndSalty.getLeft();
        String salty = passwordAndSalty.getRight();
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-224");
            crypt.update((pepper+user.getPassword()+salty).getBytes(StandardCharsets.UTF_8));
            byte[] bytes = crypt.digest();
            return realPassword.equals(new String(bytes, StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public void register(User user) throws UsernameAlreadyTakenException, SQLException,
            InvalidUsernameException, InvalidPasswordException {
        if(user.getUsername().length()<=3 || user.getUsername().length()>40)
            throw new InvalidUsernameException("имя пользователя должно быть от 4 ло 40 символов");
        if(user.getPassword().length()<=3 || user.getPassword().length()>40)
            throw new InvalidPasswordException("пароль должен быть от 4 ло 40 символов");
        if(databaseManager.usernameExist(user.getUsername()))
            throw new UsernameAlreadyTakenException("имя " +user.getUsername() + " уже занято");
        MessageDigest crypt;
        try {
            crypt = MessageDigest.getInstance("SHA-224");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] array = new byte[10];
        new Random().nextBytes(array);
        String salty = new String(array, StandardCharsets.UTF_8);
        crypt.update((pepper+user.getPassword()+salty).getBytes(StandardCharsets.UTF_8));
        byte[] bytes = crypt.digest();
        lock.lock();
        try {
            databaseManager.insertUser(user.getUsername(), new String(bytes, StandardCharsets.UTF_8), salty);
            logger.info("новый пользователь зарегистрировался");
        }
        finally {
            lock.unlock();
        }
    }
    private boolean idExist(int id){
        for (StudyGroup studyGroup: studyGroupMap.values()){
            if (studyGroup.getId()==id) return true;
        }
        return false;
    }
}
