package com.alame.lab7.server.input.readers.commads.console;

import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.exceptions.CommandNotFoundException;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.parsers.CommandParser;
import com.alame.lab7.server.input.readers.ConsoleReader;
import com.alame.lab7.server.input.readers.commads.CommandReader;

public class ConsoleCommandReader extends ConsoleReader implements CommandReader {
    private final CommandParser commandParser;
    public ConsoleCommandReader(CommandParser commandParser){
        this.commandParser = commandParser;
    }
    /**
     * read command from console
     * @return received command
     */
    public Command readCommand() throws IncorrectCommandParameterException, CommandNotFoundException{
        String nextLine = getNextLine();
        return commandParser.parseCommand(nextLine);
    }
}
