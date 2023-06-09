package com.alame.lab7.server.database;


import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManagerImpl implements DatabaseConnectionManager {
    private final BasicDataSource basicDataSource = new BasicDataSource();
    public DatabaseConnectionManagerImpl(String url, String user, String password){
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(user);
        basicDataSource.setPassword(password);
    }
    @Override
    public Connection getConnection() throws SQLException {
        return basicDataSource.getConnection();
    }
    @Override
    public void close() throws SQLException{
        basicDataSource.close();
    }
}
