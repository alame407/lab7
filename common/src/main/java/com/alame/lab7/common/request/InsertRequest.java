package com.alame.lab7.common.request;

import com.alame.lab7.common.data.StudyGroup;
import com.alame.lab7.common.exceptions.IncorrectElementFieldException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.exceptions.UserAccessException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for insert pair key, element in collection
 */
public class InsertRequest extends AbstractRequest{
    StudyGroup studyGroup;
    String key;
    public InsertRequest(StudyGroup studyGroup, String key, User user){
        super(user);
        this.studyGroup = studyGroup;
        this.key = key;
    }

    /**
     * insert pair key, element in collection
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try {
            getServer().insert(key, studyGroup, user);
            return new Response<>(ResponseStatus.SUCCESS, "Команда выполнена успешно", null);
        }catch (IncorrectElementFieldException | NoSuchUserException | UserAccessException e){
           return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
        catch (SQLException e) {
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        }
    }

    @Override
    public String toString() {
        return "InsertRequest";
    }
}
