package com.alame.lab7.server.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnectionManager {
	Connection getConnection() throws SQLException;

	void close() throws SQLException;
}
