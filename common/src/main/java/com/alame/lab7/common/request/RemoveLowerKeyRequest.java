package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.DatabaseUpdateException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for delete all keys which less than iven
 */
public class RemoveLowerKeyRequest extends AbstractRequest{
    private final String key;

    public RemoveLowerKeyRequest(String key, User user) {
        super(user);
        this.key = key;
    }

    /**
     * delete all keys which less than iven
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try {
            getServer().removeLowerKey(key, user);
            return new Response<>(ResponseStatus.SUCCESS, "Команда выполнена успешно", null);
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        } catch (NoSuchUserException | DatabaseUpdateException e) {
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }

    }
}
