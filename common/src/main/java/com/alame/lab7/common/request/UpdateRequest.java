package com.alame.lab7.common.request;

import com.alame.lab7.common.data.Coordinates;
import com.alame.lab7.common.data.FormOfEducation;
import com.alame.lab7.common.data.Person;
import com.alame.lab7.common.data.Semester;
import com.alame.lab7.common.exceptions.DatabaseUpdateException;
import com.alame.lab7.common.exceptions.IncorrectElementFieldException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.exceptions.UserAccessException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

/**
 * request for updating fields of element by id
 */
public class UpdateRequest extends AbstractRequest{
    private final int id;
    private final String name;
    private final Coordinates coordinates;
    private final int studentsCount;
    private final long expelledStudents;
    private final FormOfEducation formOfEducation;
    private final Semester semester;
    private final Person groupAdmin;
    public UpdateRequest(int id, String name, Coordinates coordinates, int studentsCount, long expelledStudents,
                         FormOfEducation formOfEducation, Semester semester, Person groupAdmin, User user){
        super(user);
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.studentsCount = studentsCount;
        this.expelledStudents = expelledStudents;
        this.formOfEducation = formOfEducation;
        this.semester = semester;
        this.groupAdmin = groupAdmin;
    }

    /**
     * updating fields of element by id
     * @return generated response
     */
    @Override
    public Response<String> handle() {
        try {
            getServer().update(id, name, coordinates, studentsCount, expelledStudents, formOfEducation,
                    semester, groupAdmin, user);
            return new Response<>(ResponseStatus.SUCCESS, "Команда выполнена успешно", null);
        }
        catch (IncorrectElementFieldException | UserAccessException | NoSuchUserException | DatabaseUpdateException e){
            return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
        }
        catch (SQLException e){
            return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
        }
    }

    @Override
    public String toString() {
        return "UpdateRequest";
    }
}
