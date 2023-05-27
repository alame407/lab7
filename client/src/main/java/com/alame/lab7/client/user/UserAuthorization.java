package com.alame.lab7.client.user;

import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.RegisterRequest;
import com.alame.lab7.common.request.SignInRequest;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;
import com.alame.lab7.common.user.User;

import java.io.IOException;

public class UserAuthorization {
	private final Printer printer;
	private final UserReader userReader;
	private final RequestSender requestSender;

	public UserAuthorization(Printer printer, UserReader userReader, RequestSender requestSender) {
		this.printer = printer;
		this.userReader = userReader;
		this.requestSender = requestSender;
	}

	public User authorize(){
		printer.printlnString("Если вы хотите зарегистрироваться введите registration, " +
				"если войти любую другую последовательность символов");
		if (userReader.readLine().equals("registration")) return register();
		return signIn();
	}
	private User register(){
		User user = readUser();
		try {
			Response<String> response = requestSender.sendThenReceive(new RegisterRequest(user));
			if (response.getStatus()== ResponseStatus.SUCCESS) {
				printer.printlnString(response.getResponse());
				return user;
			}
			printer.printlnString(response.getErrors());
			return authorize();
		} catch (IOException e) {
			printer.printlnString("не удалось получить ответ сервера повторите попытку позже");
			return authorize();
		}
	}
	private User signIn(){
		User user = readUser();
		try {
			Response<String> response = requestSender.sendThenReceive(new SignInRequest(user));
			if (response.getStatus()== ResponseStatus.SUCCESS) {
				printer.printlnString(response.getResponse());
				return user;
			}
			printer.printlnString(response.getErrors());
			return authorize();
		} catch (IOException e) {
			printer.printlnString("не удалось получить ответ сервера повторите попытку позже");
			return authorize();
		}
	}
	private User readUser(){
		printer.printlnString("Введите имя пользователя(от 4 до 40 символов)");
		String username = userReader.readUsername();
		while(username.length()<=3||username.length()>40){
			printer.printlnString("имя пользователя должно содержать от 4 до 40 символов");
			username = userReader.readUsername();
		}
		printer.printlnString("Введите пароль(от 4 до 40 символов)");
		String password = userReader.readPassword();
		while(password.length()<=3||password.length()>40){
			printer.printlnString("пароль должен содержать от 4 до 40 символов");
			password = userReader.readPassword();
		}
		return new User(username, password);
	}
}
