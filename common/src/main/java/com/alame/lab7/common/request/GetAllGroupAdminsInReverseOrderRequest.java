package com.alame.lab7.common.request;

import com.alame.lab7.common.data.Person;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;
import java.util.List;

/**
 * request for get all group admins of study groups in reverse order
 */
public class GetAllGroupAdminsInReverseOrderRequest extends AbstractRequest{
    public GetAllGroupAdminsInReverseOrderRequest(User user) {
        super(user);
    }

    /**
     * get all group admins of study groups in reverse order
     * @return generated response
     */
    @Override
    public Response<List<Person>> handle() {
        try {
            List<Person> personList = getServer().getAllGroupAdminsInReverseOrder(user);
            return new Response<>(ResponseStatus.SUCCESS, personList, null);
        }
        catch (SQLException e) {
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        } catch (NoSuchUserException e) {
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }

    }
}
