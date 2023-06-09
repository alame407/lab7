package com.alame.lab7.common.request;

import com.alame.lab7.common.exceptions.IdNotExistException;
import com.alame.lab7.common.exceptions.KeyNotExistException;
import com.alame.lab7.common.exceptions.NoSuchUserException;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.sql.SQLException;

public class CheckAccessKeyRequest extends AbstractRequest{
	private final String key;
	public CheckAccessKeyRequest(User user, String key){
		super(user);
		this.key=key;
	}
	@Override
	public Response<Boolean> handle() {
		try{
			Boolean result = getServer().checkAccess(user, key);
			return new Response<>(ResponseStatus.SUCCESS, result, null);
		} catch (SQLException e) {
			return new Response<>(ResponseStatus.FAIL, null, "не удалось выполнить запрос к базе данных");
		} catch (NoSuchUserException | KeyNotExistException e) {
			return new Response<>(ResponseStatus.FAIL, null, e.getMessage());
		}
	}
}
