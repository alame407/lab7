package com.alame.lab7.client;

import com.alame.lab7.client.input.UserInput;
import com.alame.lab7.client.input.readers.commands.console.ConsoleCommandReader;
import com.alame.lab7.client.input.readers.elements.console.ConsoleCoordinatesReader;
import com.alame.lab7.client.input.readers.elements.console.ConsolePersonReader;
import com.alame.lab7.client.input.readers.elements.console.ConsoleStudyGroupReader;
import com.alame.lab7.client.user.UserAuthorization;
import com.alame.lab7.client.user.UserReader;
import com.alame.lab7.client.utility.network.RequestSender;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.commands.CommandMapper;
import com.alame.lab7.common.data.CoordinatesValidator;
import com.alame.lab7.common.data.PersonValidator;
import com.alame.lab7.common.data.StudyGroupValidator;
import com.alame.lab7.common.exceptions.CommandNotFoundException;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.parsers.CommandParser;
import com.alame.lab7.common.parsers.CoordinatesParser;
import com.alame.lab7.common.parsers.PersonParser;
import com.alame.lab7.common.parsers.StudyGroupParser;
import com.alame.lab7.common.printers.ConsolePrinter;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.client.commands.*;
import com.alame.lab7.common.user.User;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    private final CommandHandler commandHandler;
    private final Printer printer;
    private final UserInput userInput;
    public App(Printer printer, CommandHandler commandHandler, RequestSender requestSender, CommandMapper commandMapper,
               UserAuthorization userAuthorization)
            throws IOException {
        this.printer = printer;
        this.commandHandler = commandHandler;
        CommandParser commandParser = new CommandParser(commandMapper);
        PersonValidator personValidator = new PersonValidator();
        CoordinatesValidator coordinatesValidator = new CoordinatesValidator();
        StudyGroupValidator studyGroupValidator = new StudyGroupValidator(personValidator, coordinatesValidator);
        CoordinatesParser coordinatesParser = new CoordinatesParser(coordinatesValidator);
        PersonParser personParser = new PersonParser(personValidator);
        StudyGroupParser studyGroupParser = new StudyGroupParser(studyGroupValidator);
        this.userInput = new UserInput(new ConsoleCommandReader(printer, commandParser),
                new ConsoleStudyGroupReader(printer,new ConsolePersonReader(printer, personParser),
                        new ConsoleCoordinatesReader(printer, coordinatesParser), studyGroupParser));
        User user = userAuthorization.authorize();
        Map<String, Command> stringToCommand = new HashMap<>()
        {
            {
                put("help", new HelpCommand(printer, commandMapper));
                put("info", new InfoCommand(printer, requestSender, user));
                put("insert", new InsertCommand(userInput,requestSender, printer, user));
                put("show", new ShowCommand(printer, requestSender, user));
                put("update", new UpdateCommand(userInput, printer, requestSender, user));
                put("remove_key", new RemoveKeyCommand(requestSender, printer, user));
                put("clear", new ClearCommand(requestSender, printer, user));
                put("execute_script", new ExecuteScriptCommand(userInput,commandHandler, printer,
                        commandParser, coordinatesParser, personParser, studyGroupParser));
                put("exit", new ExitCommand());
                put("history", new HistoryCommand(commandHandler, printer));
                put("replace_if_lower", new ReplaceIfLowerCommand(userInput, requestSender, printer, user));
                put("remove_lower_key", new RemoveLowerKeyCommand(requestSender, printer, user));
                put("remove_all_by_form_of_education", new RemoveAllByFormOfEducationCommand(userInput, requestSender, printer, user));
                put("max_by_creation_date", new MaxByCreationDateCommand(printer, requestSender, user));
                put("print_field_descending_group_admin", new PrintFieldDescendingGroupAdminCommand(printer, requestSender, user));
            }
        };
        commandMapper.addAllCommands(stringToCommand);
    }
    public void start(){
        while (true){
            try {
                Command command = userInput.getCommandReader().readCommand();
                commandHandler.handle(command);
            } catch (IncorrectCommandParameterException | CommandNotFoundException e) {
                printer.printlnString(e.getMessage());
            }

        }
    }
    public static void main(String[] args) {
        Printer printer = new ConsolePrinter();
        try(DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.setSoTimeout(10000);
            RequestSender requestSender = new RequestSender(datagramSocket);
            new App(printer, new CommandHandler(),
                    requestSender, new CommandMapper(),
                    new UserAuthorization(new ConsolePrinter(), new UserReader(new Scanner(System.in)), requestSender))
                    .start();
        }
        catch (IOException e){
            printer.printlnString(e.getMessage()+ " не удалось запустить приложение");
            System.exit(0);
        }
    }
}