package com.alame.lab7.server.input.readers;

import java.util.Scanner;

public class ConsoleReader {
    private final Scanner scanner = new Scanner(System.in);
    public String getNextLine(){
        if (scanner.hasNextLine()){
            return scanner.nextLine();
        }
        System.exit(0);
        return null;
    }
}
