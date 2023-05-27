package com.alame.lab7.common.request;

import com.alame.lab7.common.data.FormOfEducation;
import com.alame.lab7.common.exceptions.DatabaseUpdateException;
import com.alame.lab7.common.exceptions.IncorrectElementFieldException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for remove all study groups by form of education
 */
public class RemoveAllByFormOfEducation extends AbstractRequest{
    private final FormOfEducation formOfEducation;

    public RemoveAllByFormOfEducation(FormOfEducation formOfEducation, User user) {
        super(user);
        this.formOfEducation = formOfEducation;
    }

    /**
     * remove all study groups by form of education
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try {
            getServer().removeAllByFormOfEducation(formOfEducation, user);
            return new Response<>(ResponseStatus.SUCCESS, "Команда выполнена успешно", null);
        }
        catch (IncorrectElementFieldException | NoSuchUserException | DatabaseUpdateException e){
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        }
    }
}
