package com.alame.lab7.client.commands;

import com.alame.lab7.client.input.UserInput;
import com.alame.lab7.common.user.User;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.exceptions.IncorrectElementFieldException;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.RemoveAllByFormOfEducation;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;

import java.io.IOException;

/**
 * command for removing all elements with same formOfEducation
 */
public class RemoveAllByFormOfEducationCommand implements Command {
    private final UserInput userInput;
    private final RequestSender requestSender;
    private final Printer printer;
    private final User user;
    public RemoveAllByFormOfEducationCommand(UserInput userInput, RequestSender requestSender, Printer printer, User user){
        this.userInput = userInput;
        this.requestSender = requestSender;
        this.printer = printer;
        this.user = user;
    }

    /**
     * remove all elements in collection with same formOfEducation
     * @return success of execution
     */
    @Override
    public boolean execute() {
        try {
            Response<String> response = requestSender.sendThenReceive(
                    new RemoveAllByFormOfEducation(userInput.getStudyGroupReader().readFormOfEducation(), user));
            if (response.getStatus()== ResponseStatus.SUCCESS){
                printer.printlnString(response.getResponse());
                return true;
            }
            else{
                printer.printlnString(response.getErrors());
                return false;
            }
        }
        catch (IOException | IncorrectElementFieldException e){
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
        if (parameters.length!=0) throw new IncorrectCommandParameterException("Данная команда не принимает аргументов, " +
                "поле formOfEducation вводится на следующей строке");
    }

    /**
     * @return command description
     */
    @Override
    public String description() {
        return "remove_all_by_form_of_education {formOfEducation}: удаляет из коллекции все элементы, " +
                "значение поля formOfEducation которого эквивалентно заданному";
    }

    /**
     * @return command name
     */
    @Override
    public String name() {
        return "remove_all_by_form_of_education";
    }

    /**
     * create new RemoveAllByFormOfEducationCommand
     * @return new RemoveAllByFormOfEducationCommand with same receiver and userInput
     */
    @Override
    public Command newInstance() {
        return new RemoveAllByFormOfEducationCommand(userInput, requestSender, printer, user);
    }
}
