package com.alame.lab7.server.database;


import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private final BasicDataSource ds = new BasicDataSource();
    public DatabaseConnectionManager(String url, String user, String password){
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
    }
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
