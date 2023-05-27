package com.alame.lab7.common.request;

import com.alame.lab7.common.data.StudyGroup;
import com.alame.lab7.common.exceptions.*;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for replace element by key if element less than given
 */
public class ReplaceIfLowerRequest extends AbstractRequest{
    private final StudyGroup studyGroup;
    private final String key;

    public ReplaceIfLowerRequest(StudyGroup studyGroup, String key, User user) {
        super(user);
        this.studyGroup = studyGroup;
        this.key = key;
    }

    /**
     * replace element by key if element less than given
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try {
            getServer().replaceIfLower(key, studyGroup, user);
            return new Response<>(ResponseStatus.SUCCESS, "Команда выполнена успешно", null);
        }
        catch (IncorrectElementFieldException | UserAccessException | NoSuchUserException | DatabaseUpdateException e){
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        }
    }
}
