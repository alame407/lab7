package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.IdNotExistException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

public class CheckAccessIdRequest extends AbstractRequest{
	private final int id;
	public CheckAccessIdRequest(User user, int id){
		super(user);
		this.id=id;
	}
	@Override
	public Response<Boolean> handle() {
		try {
			Boolean result = getServer().checkAccess(user, id);
			return new Response<>(ResponseStatus.SUCCESS, result, null);
		} catch (SQLException e) {
			return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
		} catch (NoSuchUserException | IdNotExistException e) {
			return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
		}
	}
}
