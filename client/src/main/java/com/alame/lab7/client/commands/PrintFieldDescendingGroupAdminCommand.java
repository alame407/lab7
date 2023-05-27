package com.alame.lab7.client.commands;

import com.alame.lab7.common.user.User;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.data.Person;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.common.request.GetAllGroupAdminsInReverseOrderRequest;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.common.response.ResponseStatus;

import java.io.IOException;
import java.util.List;

/**
 * command for showing all groupAdmins in revers order
 */
public class PrintFieldDescendingGroupAdminCommand implements Command {
    private final Printer printer;
    private final RequestSender requestSender;
    private final User user;
    public PrintFieldDescendingGroupAdminCommand(Printer printer, RequestSender requestSender, User user){
        this.printer = printer;
        this.requestSender = requestSender;
        this.user = user;
    }

    /**
     * show all groupAdmins in reverse order
     * @return success of execution
     */
    @Override
    public boolean execute() {
        try{
            Response<List<Person>> response = requestSender.sendThenReceive(
                    new GetAllGroupAdminsInReverseOrderRequest(user));
            if (response.getStatus()== ResponseStatus.SUCCESS){
                for (Person person: response.getResponse()){
                    printer.printlnString(person.toString());
                }
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
        return "print_field_descending_group_admin: выводит значения поля groupAdmin всех элементов в порядке убывания";
    }
    /**
     * @return command name
     */
    @Override
    public String name() {
        return "print_field_descending_group_admin";
    }

    /**
     * create new PrintFieldDescendingGroupAdminCommand
     * @return new PrintFieldDescendingGroupAdminCommand with same receiver
     */
    @Override
    public Command newInstance() {
        return new PrintFieldDescendingGroupAdminCommand(printer, requestSender, user);
    }
}
