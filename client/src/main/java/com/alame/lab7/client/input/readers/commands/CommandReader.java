package com.alame.lab7.client.input.readers.commands;

import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.exceptions.CommandNotFoundException;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;

/**
 * interface for all class that read commands
 */
public interface CommandReader {
    /**
     * read command
     * @return received command
     * @throws IncorrectCommandParameterException if command parameters are not valid
     */
    Command readCommand() throws IncorrectCommandParameterException, CommandNotFoundException;
}
