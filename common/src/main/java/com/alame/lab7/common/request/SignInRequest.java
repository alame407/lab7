package com.alame.lab7.common.request;

import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

public class SignInRequest extends AbstractRequest{
	public SignInRequest(User user) {
		super(user);
	}

	@Override
	public Response<?> handle() {
		try {
			if(getServer().signIn(user)){
				return new Response<>(ResponseStatus.SUCCESS, "Регистрация прошла успешно", null);
			}
			return new Response<>(ResponseStatus.FAIL, null, "Неверный пароль или имя пользователя");
		}
		catch (SQLException e){
			return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
		}
	}
}
