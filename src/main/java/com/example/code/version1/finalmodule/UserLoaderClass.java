package com.example.code.version1.finalmodule ;


import com.example.code.version1.dto.*;

import java.util.concurrent.*;

import java.util.concurrent.ConcurrentHashMap;

import java.util.*;

import java.util.HashMap ;

import java.util.Map ;

import java.sql.*;

import java.sql.Connection ;

import java.sql.PreparedStatement ;

import java.sql.Statement ;

import java.sql.ResultSet ;

import java.sql.DriverManager ;



public class UserLoaderClass {


public static  void userLoader ( ConcurrentHashMap < String ,  User> dummyusers  , Connection connection )

{


 String sqlquery = "SELECT * FROM users " ;
 

User user ; 

try 

{

PreparedStatement  psmt  = connection.prepareStatement ( sqlquery);

ResultSet rs = psmt.executeQuery () ;


while ( rs.next () )
{


user = new User(

    rs.getString("username"),
    rs.getString("name"),
    rs.getString("email"),
    Role.valueOf(rs.getString("role")),
    "ACTIVE".equals(rs.getString("status") ),
    rs.getString("password_hash"),
    rs.getString("payment_password_hash")

);


dummyusers.put (  user.getUsername () , user );

}

 } 

 catch ( SQLException e )
 {


System.err.println("The error is :: " + e ) ;


 }
}

}