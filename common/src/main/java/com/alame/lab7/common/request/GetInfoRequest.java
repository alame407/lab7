package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for get info about collection
 */
public class GetInfoRequest extends AbstractRequest{
    public GetInfoRequest(User user) {
        super(user);
    }

    /**
     * get info about collection
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try {
            return new Response<>(ResponseStatus.SUCCESS, getServer().info(user), null);
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        } catch (NoSuchUserException e) {
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
    }
}
