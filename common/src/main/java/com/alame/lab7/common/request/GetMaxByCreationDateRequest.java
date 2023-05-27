package com.alame.lab7.common.request;

import com.alame.lab7.common.data.StudyGroup;
import com.alame.lab7.common.exceptions.CollectionIsEmptyException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for get max study group by creation date
 */
public class GetMaxByCreationDateRequest extends AbstractRequest{
    public GetMaxByCreationDateRequest(User user) {
        super(user);
    }

    /**
     * get max study group by creation date
     * @return generated response
     */
    @Override
    public Response<StudyGroup> handle() {
        try {
            StudyGroup studyGroup = getServer().getMaxByCreationDate(user);
            return new Response<>(ResponseStatus.SUCCESS, studyGroup, null);
        }
        catch (CollectionIsEmptyException | NoSuchUserException e){
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
        catch (SQLException e) {
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        }
    }

    @Override
    public String toString() {
        return "GetMaxByCreationDateRequest";
    }
}
