package com.cinephile.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Hardcoded for demo purposes; ordinarily these might be in a config file
    private static final String URL = "jdbc:mysql://localhost:3308/cinephile_db";
    private static final String USER = "root";
    private static final String PASSWORD = "rootpassword";

    public static Connection getConnection() throws SQLException {
        try {
            // Load Driver explicitly just to be safe with older JREs/JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
