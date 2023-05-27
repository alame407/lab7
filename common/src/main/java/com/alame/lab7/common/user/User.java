package com.alame.lab7.common.user;

import java.io.Serializable;

public class User implements Serializable {
	private String username;
	private String password;
	public User(String username, String password){
		this.password = password;
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}
}
