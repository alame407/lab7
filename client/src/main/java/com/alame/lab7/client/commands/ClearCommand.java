package com.alame.lab7.client.commands;


import com.alame.lab7.common.user.User;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.ClearRequest;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;

import java.io.IOException;

/**
 * Command for clear collection
 */

public class ClearCommand implements Command {
    private final RequestSender requestSender;
    private final Printer printer;
    private final User user;
    public ClearCommand(RequestSender requestSender, Printer printer, User user){
        this.requestSender = requestSender;
        this.printer = printer;
        this.user = user;
    }

    /**
     * clear collection
     * @return success of execution
     */
    @Override
    public boolean execute(){
        try {
            Response<String> response = requestSender.sendThenReceive(
                    new ClearRequest(user));
            if (response.getStatus() == ResponseStatus.SUCCESS){
                printer.printlnString(response.getResponse());
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
        return "clear: очищает коллекцию";
    }

    /**
     * @return command name
     */
    @Override
    public String name() {
        return "clear";
    }

    /**
     * create new Instance of ClearCommand
     * @return new ClearCommand with same receiver
     */
    @Override
    public Command newInstance() {
        return new ClearCommand(requestSender, printer, user);
    }

}
