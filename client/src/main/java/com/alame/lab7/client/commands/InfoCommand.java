package com.alame.lab7.client.commands;

import com.alame.lab7.common.user.User;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.GetInfoRequest;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;

import java.io.IOException;

/**
 * command for show info about collection
 */
public class InfoCommand implements Command {
    private final Printer printer;
    private final RequestSender requestSender;
    private final User user;
    public InfoCommand(Printer printer, RequestSender requestSender, User user){
        this.printer = printer;
        this.requestSender = requestSender;
        this.user = user;
    }

    /**
     * show information about collection
     * @return success of execution
     */
    @Override
    public boolean execute() {
        try{
            Response<String> response = requestSender.sendThenReceive(
                    new GetInfoRequest(user));
            if (response.getStatus()== ResponseStatus.SUCCESS){
                printer.printlnString(response.getResponse());
                return true;
            }
            else{
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
        return "info: выводит информацию о коллекции";
    }
    /**
     * @return command name
     */
    @Override
    public String name() {
        return "info";
    }

    /**
     * create new IndoCommand
     * @return new InfoCommand with same receiver
     */
    @Override
    public Command newInstance() {
        return new InfoCommand(printer, requestSender, user);
    }
}
