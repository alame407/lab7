package com.alame.lab7.common.request;

import com.alame.lab7.common.data.StudyGroup;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;
import java.util.List;

/**
 * request for get all study groups
 */
public class GetAllValuesRequest extends AbstractRequest{
    public GetAllValuesRequest(User user) {
        super(user);
    }

    /**
     * get all study groups
     * @return generated response
     */
    @Override
    public Response<List<StudyGroup>> handle() {
        try {
            return new Response<>(ResponseStatus.SUCCESS, getServer().getAllValues(user), null);
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        } catch (NoSuchUserException e) {
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }

    }

    @Override
    public String toString() {
        return "GetAllValuesRequest";
    }
}
