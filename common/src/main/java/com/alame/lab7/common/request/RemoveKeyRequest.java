package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.DatabaseUpdateException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.exceptions.UserAccessException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for remove key from collection
 */
public class RemoveKeyRequest extends AbstractRequest{
    private final String key;

    public RemoveKeyRequest(String key, User user) {
        super(user);
        this.key = key;
    }

    /**
     * remove key from collection
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try {
            getServer().removeKey(key, user);
            return new Response<>(ResponseStatus.SUCCESS, "Команда выполнена успешно", null);
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        } catch (UserAccessException | NoSuchUserException | DatabaseUpdateException e) {
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }

    }
}
