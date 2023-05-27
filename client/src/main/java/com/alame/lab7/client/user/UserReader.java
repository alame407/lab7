package com.alame.lab7.client.user;

import java.util.Scanner;

public class UserReader {
	private final Scanner scanner;

	public UserReader(Scanner scanner) {
		this.scanner = scanner;
	}
	public String readUsername(){
		if(scanner.hasNextLine()){
			return scanner.nextLine();
		}
		System.exit(0);
		return null;
	}
	public String readPassword(){
		if(scanner.hasNextLine()){
			return scanner.nextLine();
		}
		System.exit(0);
		return null;
	}
	public String readLine(){
		if(scanner.hasNextLine()){
			return scanner.nextLine();
		}
		System.exit(0);
		return null;
	}
}
