package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.DatabaseUpdateException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * clear collection request
 */
public class ClearRequest extends AbstractRequest{
    public ClearRequest(User user) {
        super(user);
    }

    /**
     * clear collection
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try{
            getServer().clear(user);
            return new Response<>(ResponseStatus.SUCCESS, "Команда выполнена успешно", null);
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        } catch (NoSuchUserException | DatabaseUpdateException e) {
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "ClearRequest";
    }
}
