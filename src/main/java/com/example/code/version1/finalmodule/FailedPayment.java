package com.example.code.version1.finalmodule ;

import java.sql.Connection ;

import java.sql.SQLException ;

import java.sql.PreparedStatement ;

import java.sql.DriverManager ;

import java.sql.Statement ;

import java.sql.ResultSet ;

import java.sql.ResultSetMetaData ;

import java.sql.DatabaseMetaData ;



public class FailedPayment {


public  static  void insertFailedPayment ( Connection connection , String  fromUsername , String request_id , String reason  )
{


StringBuilder query  = new StringBuilder
 ( " insert into failed_requests   ( request_id , username , reason) values ( ? , ? , ? )    ; ");


try {


PreparedStatement psmt  ;



psmt  = connection.prepareStatement ( query.toString() ) ;

psmt.setString ( 1 , request_id );

psmt.setString ( 2 , fromUsername    );

psmt.setString ( 3 , reason );

 psmt.executeUpdate();



    psmt.close();



}

catch ( SQLException e )


{
System.out.println(e);

}



}


}