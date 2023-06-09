package com.alame.lab7.client.commands;

import com.alame.lab7.client.input.UserInput;
import com.alame.lab7.common.request.KeyExistRequest;
import com.alame.lab7.common.user.User;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.exceptions.IncorrectElementFieldException;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.InsertRequest;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;

import java.io.IOException;

/**
 * command for insert a pair of key and element in collection
 */
public class InsertCommand implements Command {
    private final UserInput userInput;
    private final RequestSender requestSender;
    private final Printer printer;
    private String key;
    private final User user;
    public InsertCommand(UserInput userInput, RequestSender requestSender, Printer printer, User user){
        this.userInput = userInput;
        this.requestSender = requestSender;
        this.printer = printer;
        this.user = user;
    }

    /**
     * insert a pair of key and element in collection
     * @return success of execution
     */
    @Override
    public boolean execute(){
        try{
            Response<String> response = requestSender.sendThenReceive(
                    new InsertRequest(userInput.getStudyGroupReader().readStudyGroup(), key, user));
            if (response.getStatus() == ResponseStatus.SUCCESS){
                printer.printlnString(response.getResponse());
                return true;
            }
            else{
                printer.printlnString(response.getErrors());
                return false;
            }
        }catch (IncorrectElementFieldException | IOException e){
            printer.printlnString(e.getMessage());
            return false;
        }
    }

    /**
     * set key
     * @param parameters - all parameters of command
     * @throws IncorrectCommandParameterException if parameters size!=1
     */
    @Override
    public void setParameters(String[] parameters) throws IncorrectCommandParameterException {
        if (parameters.length!=1) throw new IncorrectCommandParameterException("Данная команда принимает 1 аргумент");
        key = parameters[0];
        try {
            Response<Boolean> response = requestSender.sendThenReceive(new KeyExistRequest(key, user));
            if (response.getStatus()!=ResponseStatus.SUCCESS)
                throw new IncorrectCommandParameterException("не удалось выполнить запрос");
            if (response.getResponse())
                throw new IncorrectCommandParameterException("такой ключ уже существует");
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
        return "insert key: добавляет новый элемент с заданным ключом";
    }
    /**
     * @return command description
     */
    @Override
    public String name() {
        return "insert";
    }

    /**
     * create new InsertCommand
     * @return new InsertCommand with same receiver and userInput
     */
    @Override
    public Command newInstance() {
        return new InsertCommand(userInput, requestSender, printer, user);
    }
}
