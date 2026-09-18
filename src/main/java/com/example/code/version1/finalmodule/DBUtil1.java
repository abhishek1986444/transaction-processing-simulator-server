package com.example.code.version1.finalmodule ;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.example.configservice.ConfigService;


public class DBUtil1 {


       private static final String URL = ConfigService.getSecurity("db.url");


    private static final String USER = ConfigService.getSecurity("db.user");


    private static final String PASSWORD =  ConfigService.getSecurity("db.password") ;



    public  static Connection getConnection() throws SQLException, ClassNotFoundException {

Class.forName("com.mysql.cj.jdbc.Driver");



        return DriverManager.getConnection(URL, USER, PASSWORD);

    }
}

