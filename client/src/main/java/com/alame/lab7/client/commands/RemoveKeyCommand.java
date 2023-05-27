package com.alame.lab7.client.commands;

import com.alame.lab7.common.user.User;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.KeyExistRequest;
import com.alame.lab7.common.request.RemoveKeyRequest;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;

import java.io.IOException;

/**
 * command for removing key from collection
 */
public class RemoveKeyCommand implements Command {
    private String key;
    private final RequestSender requestSender;
    private final Printer printer;
    private final User user;

    public RemoveKeyCommand(RequestSender requestSender, Printer printer, User user) {
        this.requestSender = requestSender;
        this.printer = printer;
        this.user = user;
    }

    /**
     * remove key from collection
     * @return success of execution
     */
    @Override
    public boolean execute() {
        try {
            Response<String> response = requestSender.sendThenReceive(
                    new RemoveKeyRequest(key, user));
            if (response.getStatus()== ResponseStatus.SUCCESS){
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
     * set key
     * @param parameters - all parameters of command
     * @throws IncorrectCommandParameterException if parameters size!=1 or key doesn't exist
     */
    @Override
    public void setParameters(String[] parameters) throws IncorrectCommandParameterException {
        if (parameters.length!=1) throw new IncorrectCommandParameterException("Данная команда принимает 1 аргумент");
        try{
            key = parameters[0];
            Response<Boolean> response = requestSender.sendThenReceive(new KeyExistRequest(key, user));
            if (!response.getResponse()){
                throw new IncorrectCommandParameterException("такого ключа не существует");
            }
        }
        catch (IOException e){
            throw new IncorrectCommandParameterException(e.getMessage());
        }

    }

    /**
     * @return command description
     */
    @Override
    public String description() {
        return "remove_key key: Удаляет элемент коллекции с заданным ключом";
    }

    /**
     * @return command name
     */
    @Override
    public String name() {
        return "remove_key";
    }

    /**
     * create new RemoveKeyCommand
     * @return new RemoveKeyCommand with same receiver
     */
    @Override
    public Command newInstance() {
        return new RemoveKeyCommand(requestSender, printer, user);
    }
}
