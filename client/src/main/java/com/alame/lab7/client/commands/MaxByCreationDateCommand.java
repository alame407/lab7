package com.alame.lab7.client.commands;

import com.alame.lab7.common.user.User;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.data.StudyGroup;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.GetMaxByCreationDateRequest;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;

import java.io.IOException;

/**
 * command for showing max element by creation date
 */
public class MaxByCreationDateCommand implements Command {
    private final Printer printer;
    private final RequestSender requestSender;
    private final User user;
    public MaxByCreationDateCommand(Printer printer, RequestSender requestSender, User user){
        this.printer = printer;
        this.requestSender = requestSender;
        this.user = user;
    }

    /**
     * show element of collection with max creation date
     * @return success of execution
     */
    @Override
    public boolean execute() {
        try {
            Response<StudyGroup> response = requestSender.sendThenReceive(
                    new GetMaxByCreationDateRequest(user));
            if (response.getStatus()== ResponseStatus.SUCCESS){
                printer.printlnStudyGroup(response.getResponse());
                return true;
            }
            else {
                printer.printlnString(response.getErrors());
                return false;
            }
        }
        catch (IOException e){
            printer.printlnString(e.getMessage());
            return false;
        }
    }

    /**
     * set no parameters
     * @param parameters - all parameters of command
     * @throws IncorrectCommandParameterException if parameters size!=0
     */
    @Override
    public void setParameters(String[] parameters) throws IncorrectCommandParameterException {
        if (parameters.length!=0) throw new IncorrectCommandParameterException("Данная команда не принимает аргументов");
    }

    /**
     * @return command description
     */
    @Override
    public String description() {
        return "max_by_creation_date: выводит любой объект из коллекции, " +
                "значение поля creationDate которого является максимальным";
    }
    /**
     * @return command name
     */
    @Override
    public String name() {
        return "max_by_creation_date";
    }

    /**
     * create new MaxByCreationDateCommand
     * @return new MaxByCreationDateCommand with same receiver
     */
    @Override
    public Command newInstance() {
        return new MaxByCreationDateCommand(printer, requestSender, user);
    }
}
