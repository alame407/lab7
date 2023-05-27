package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for check if id represented in collection
 */
public class IdExistRequest extends AbstractRequest{
    private final int id;

    public IdExistRequest(int id, User user) {
        super(user);
        this.id = id;
    }

    /**
     * check if id represented in collection
     * @return generated response
     */
    @Override
    public Response<Boolean> handle() {
        try{
            return new Response<>(ResponseStatus.SUCCESS, getServer().idExist(id, user),null);
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        } catch (NoSuchUserException e) {
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "IdExistRequest";
    }
}
