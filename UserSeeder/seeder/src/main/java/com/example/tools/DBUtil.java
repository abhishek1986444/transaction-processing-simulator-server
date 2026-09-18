package com.example.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBUtil {

    // =========================================================
    // DATABASE CONFIGURATION
    // =========================================================

    private static final String URL =
            "jdbc:mysql://localhost:3306/nettyproject2";

    private static final String USER =
            "nettyadmin";

    private static final String PASSWORD =
            "your_password";


    // =========================================================
    // GET CONNECTION
    // =========================================================

    public static Connection getConnection()
            throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}
