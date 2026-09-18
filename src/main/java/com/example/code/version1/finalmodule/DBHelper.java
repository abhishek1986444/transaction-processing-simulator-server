package com.example.code.version1.finalmodule ;

import java.sql.Connection ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;
import java.sql.DriverManager ;
import java.sql.SQLException ;
import java.sql.ResultSetMetaData ;
import  com.example.configservice.*;
import java.util.HexFormat;



public class DBHelper {


public boolean  checkUserName (String username , Connection conn  )
{
	String userstable =  ConfigService.getApp("db.userstable") ;

String sql = "SELECT username from   "  + userstable  + "  where   LOWER(username) = LOWER(?) ;  ";


try ( PreparedStatement  ps = conn.prepareStatement(sql))
{

ps.setString ( 1, username);



    try (ResultSet rs = ps.executeQuery())
    {
        return rs.next(); 
    }



}


catch ( SQLException e )
{
System.out.println("The error is " + e );
}



return false ;

}



public boolean  checkEmail (  String email  , Connection conn )

{

    String userstable =  ConfigService.getApp("db.userstable") ;



String sql = "SELECT * from " + userstable + " where  LOWER(email)  = LOWER(?)  ;  ";

try ( PreparedStatement ps = conn.prepareStatement(sql))

{

ps.setString(1,email);

ResultSet rs = ps.executeQuery();

if ( rs.next())
{



    return true ;

}

}

catch ( SQLException e )
{

System.out.println(" The error is :: " + e );
}



return false ;


}

public boolean checkUserId( String userId , Connection conn )
{

String userstable = ConfigService.getApp("db.userstable");

String sql = "select user_id from   " +  userstable  +"  where lower(user_id) = lower(?)  ;  " ;

try ( PreparedStatement ps = conn.prepareStatement(sql))
{


ps.setString ( 1 , userId);

ResultSet rs  = ps.executeQuery();


if ( rs.next())
{
    return true ;
}


}

catch (SQLException e  )
{



System.out.println("The error is :: " + e ) ;


}


return false ;


}


 public boolean  checkAccoundId ( String accountId , Connection conn )

{


String accounttable = ConfigService.getApp("db.accounttable");


String sql = "select account_id from   " +  accounttable +"  where lower(account_id) = lower(?)  ;  " ;


try ( PreparedStatement ps = conn.prepareStatement(sql))
{


ps.setString ( 1 , accountId );

ResultSet rs  = ps.executeQuery();


if ( rs.next())
{
    return true ;
}


}

catch (SQLException e  )
{

System.out.println("The error is :: " + e ) ;

}


    return false ;



}

public BalanceResult getBalance(String accountId, Connection conn)
        throws SQLException {

    String sql = "SELECT balance FROM balances WHERE account_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, accountId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new BalanceResult(true,
                    rs.getDouble("balance"));
        }

        return new BalanceResult(false, 0);
    }
}

public Boolean loginpasswordChecker( String username , String Password , Connection conn ) throws SQLException 
{

String sql = "SELECT password_hash from users where username =  ?   ;  "  ;


try ( PreparedStatement ps = conn.prepareStatement (sql ))

{
    ps.setString ( 1 , username );

    ResultSet rs = ps.executeQuery ( );

if ( rs.next())
{

  String storedHash = rs.getString("password_hash");

return PasswordUtil.verify ( Password , storedHash) ;

}
else {
 return false ;
}

}


}


public void loginpasswordUpdated  ( String username , String newPassword , Connection conn ) throws SQLException 
{

 
String sql = "update users set password_hash = ? where username =  ? ;  ";

try ( PreparedStatement ps  = conn.prepareStatement(sql))
{
    String hash = PasswordUtil.hash(newPassword);

ps.setString ( 1 , hash);

ps.setString( 2 , username);


ps.executeUpdate();


}


}











public Boolean paymentpasswordChecker( String username , String Password , Connection conn ) throws SQLException 
{

String sql = "SELECT  payment_password_hash from users where username =  ?   ;  "  ;


try ( PreparedStatement ps = conn.prepareStatement (sql ))

{
    ps.setString ( 1 , username );

    ResultSet rs = ps.executeQuery ( );

if ( rs.next())
{

  String storedHash = rs.getString("payment_password_hash");

return PasswordUtil.verify ( Password , storedHash) ;

}
else {
 return false ;
}

}


}









public void paymentpasswordUpdated  ( String username , String newPassword , Connection conn ) throws SQLException 
{

 
String sql = "update users set payment_password_hash = ? where username =  ? ;  ";

try ( PreparedStatement ps  = conn.prepareStatement(sql))
{
    String hash = PasswordUtil.hash(newPassword);

ps.setString ( 1 , hash);

ps.setString( 2 , username);


ps.executeUpdate();


}


}

public Boolean accountChecker ( String accountId , Connection conn ) throws SQLException 

{


    String sql = "select account_id from accounts where account_id = ?  ;  ";

    try (PreparedStatement ps = conn.prepareStatement(sql ))
    {
        ps.setString ( 1 , accountId );


      try (  ResultSet  rs = ps.executeQuery() ) 

     {
        if ( rs.next() )
        {



            return true ;
        }

    }

    }


        return false ;



}



public String getAccountIdByUsername ( String username  , Connection conn ) throws SQLException 
{

    String sql =  "SELECT account_id from accounts where username = ?";

try ( PreparedStatement pstmt = conn.prepareStatement(sql))
{

pstmt.setString ( 1 , username ) ;

try ( ResultSet rs = pstmt.executeQuery())
{
if ( rs.next())
{
    return rs.getString("account_id");
}
}

}


return "";

}










public boolean accountBelongsToUser(
        String username,
        String accountId,
        Connection conn
) throws SQLException {

    String sql = """
        SELECT 1
        FROM accounts
        WHERE username = ?
          AND account_id = ?
          AND account_status = 'ACTIVE'
        """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, accountId);

        try (ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}





}

