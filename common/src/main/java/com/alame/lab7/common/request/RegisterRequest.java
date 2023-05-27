package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.InvalidPasswordException;
import com.alame.lab7.common.exceptions.InvalidUsernameException;
import com.alame.lab7.common.exceptions.UsernameAlreadyTakenException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

public class RegisterRequest extends AbstractRequest{
	public RegisterRequest(User user) {
		super(user);
	}

	@Override
	public Response<String> handle() {
		try{
			getServer().register(user);
			return new Response<>(ResponseStatus.SUCCESS, "регистрация прошла успешно", null);
		} catch (SQLException e) {
			return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
		} catch (InvalidPasswordException | InvalidUsernameException | UsernameAlreadyTakenException e) {
			return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
		}
	}
}
