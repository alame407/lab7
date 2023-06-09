package com.alame.lab7.server;
import com.alame.lab7.common.commands.Command;
import com.alame.lab7.common.commands.CommandMapper;
import com.alame.lab7.common.data.CoordinatesValidator;
import com.alame.lab7.common.data.PersonValidator;
import com.alame.lab7.common.data.StudyGroupValidator;
import com.alame.lab7.common.exceptions.CommandNotFoundException;
import com.alame.lab7.common.exceptions.IncorrectCommandParameterException;
import com.alame.lab7.common.exceptions.IncorrectElementFieldException;
import com.alame.lab7.common.executors.Executor;
import com.alame.lab7.common.parsers.*;
import com.alame.lab7.common.printers.ConsolePrinter;
import com.alame.lab7.common.printers.Printer;
import com.alame.lab7.server.database.DatabaseConnectionManager;
import com.alame.lab7.server.database.DatabaseConnectionManagerImpl;
import com.alame.lab7.server.database.DatabaseManager;
import com.alame.lab7.server.commands.CommandHandler;
import com.alame.lab7.server.commands.ExitCommand;
import com.alame.lab7.server.database.DatabaseManagerImpl;
import com.alame.lab7.server.network.FrameMapper;
import com.alame.lab7.server.network.RequestHandler;
import com.alame.lab7.server.input.readers.commads.CommandReader;
import com.alame.lab7.server.input.readers.commads.console.ConsoleCommandReader;
import com.alame.lab7.server.network.RequestReceiver;
import com.alame.lab7.server.network.ResponseSender;
import com.alame.lab7.server.servers.Server;
import com.alame.lab7.server.servers.ServerInterface;
import com.alame.lab7.server.threads.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class App {
    Printer printer;
    public static final Logger logger = Logger.getLogger("ServerLogger");
    private final static int port = 50123;
    ServerInterface server;
    private final ReceiverCachedPool receiverCachedPool;
    private final RequestReceiver requestReceiver;
    public App(Printer printer, ServerInterface server, ReceiverCachedPool receiverCachedPool,
               RequestReceiver requestReceiver, DatabaseManager databaseManager) {
        this.printer = printer;
        this.server =server;
        this.requestReceiver = requestReceiver;
        this.receiverCachedPool = receiverCachedPool;
        try{
            server.putAll(databaseManager.load());
        } catch (SQLException e) {
            e.printStackTrace();
            this.printer.printlnString("Не удалось загрузить элементы из базы данных");
            System.exit(0);
        } catch (IncorrectElementFieldException e) {
            this.printer.printlnString(e.getMessage());
            System.exit(0);
        }
        try {
            FileHandler fileHandler = new FileHandler("status.log");
            logger.addHandler(fileHandler);
        }
        catch (IOException e){
            printer.printlnString(e.getMessage() + " не удалось добавить запись логов в файл");
        }
    }
    public void start(){
        logger.info("сервер запущен");
        CommandMapper commandMapper = new CommandMapper();

        CommandHandler commandHandler = new CommandHandler(new Executor());
        commandMapper.addAllCommands(new HashMap<>(){
            {
                put("exit", new ExitCommand(server, printer));
            }
        });
        CommandReader commandReader = new ConsoleCommandReader(new CommandParser(commandMapper));
        receiverCachedPool.execute(new ReceiveThread(requestReceiver));
        while(true){
            try {
                Command command = commandReader.readCommand();
                commandHandler.handle(command);
            }
            catch (IncorrectCommandParameterException | CommandNotFoundException e){
                printer.printlnString(e.getMessage());
            }
        }
    }
    public static void main(String[] args) {
        Printer printer = new ConsolePrinter();
        try (DatagramChannel datagramChannel = DatagramChannel.open()){
            datagramChannel.configureBlocking(false);
            datagramChannel.bind(new InetSocketAddress(port));
            String url = System.getenv("url");
            String username = System.getenv("user");
            String password = System.getenv("password");
            DatabaseConnectionManager databaseConnectionManager = new DatabaseConnectionManagerImpl(url, username, password);
            DatabaseManager databaseManager = new DatabaseManagerImpl(databaseConnectionManager);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    databaseConnectionManager.close();
                }
                catch (SQLException e){
                    logger.warning(e.getMessage());
                }
            }));
            ServerInterface server = new Server(new StudyGroupValidator(new PersonValidator(), new CoordinatesValidator()),
                    databaseManager);
            SendCachedPool sendCachedPool = new SendCachedPool(Executors.newCachedThreadPool());
            HandlerCachedPool handlerCachedPool = new HandlerCachedPool(Executors.newCachedThreadPool());
            ReceiverCachedPool receiverCachedPool = new ReceiverCachedPool(Executors.newCachedThreadPool());
            ResponseSender responseSender = new ResponseSender(datagramChannel);
            RequestHandler requestHandler = new RequestHandler(server, sendCachedPool, responseSender);
            RequestReceiver requestReceiver = new RequestReceiver(datagramChannel, handlerCachedPool, requestHandler,
                    new FrameMapper(new HashMap<>()));
            new App(printer, server, receiverCachedPool, requestReceiver, databaseManager).start();
        }catch (Exception e){
            logger.severe("Не удалось запустить сервер " + e.getMessage());
            System.exit(0);
        }
    }
}