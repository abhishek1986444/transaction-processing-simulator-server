package com.example.code.version1.finalmodule ;

import java.sql.Connection ;

import java.sql.SQLException ;

import java.sql.PreparedStatement ;

import java.sql.ResultSet ;

import java.time.LocalDate ;

import java.time.format.DateTimeFormatter ;

import java.sql.Date;

import java.sql.Timestamp ;

import java.time.LocalDateTime;


public class PaymentService {

 public   String  transferto (
            String fromUsername,
            String paymentpassword ,
            String toUsername,
            String fromAccount,
            String toAccount,
            double amount ,  Connection conn   , String request_id   )

 {

    try {

          DBHelper helper = new DBHelper ( );


                if ( helper.checkUserName (fromUsername , conn) )

              {

 // System.out.println("from User  is present");
               }   


            else {


return " From User is  not present ";


             }


  if ( helper.checkUserName ( toUsername , conn  ) ) 

              {

// System.out.println(" to  User  is present");
               }   


            else {


return " to  User is  not present ";


             }



       // paymentpassword

     if (   helper.paymentpasswordChecker(fromUsername ,paymentpassword , conn  )    )
     {
 //      System.out.println("The password is correct:");
     }
else {
  return "the password is incorrect.";

}



if (!helper.accountBelongsToUser(fromUsername, fromAccount, conn)) {
    return "From account does not belong to from user";
}

if (!helper.accountBelongsToUser(toUsername, toAccount, conn)) {
    return "To account does not belong to to user";
}



BalanceResult fromaccountbalance = helper.getBalance (fromAccount , conn );


if ( fromaccountbalance.balance - amount >=0.0 )
{
  //  System.out.println("It have sufficient balance\n");
}
else {
  return "In Sufficient balance from ";
}



try {


conn.setAutoCommit ( false );


  if (  !debit(fromAccount , amount , conn ) ) 

  {
    conn.rollback();
    return "Debit failed";
}


  if ( !credit ( toAccount , amount , conn  ) ) 

    {
    conn.rollback();
    return "Credit failed";
}

TransactionIdInfo info = generateTransactionId(conn);

Transaction trans = new Transaction ( 

    info.getTxnId(),

   fromUsername,
   toUsername,
   fromAccount,
   toAccount,
    amount , 
   
 Timestamp.valueOf(LocalDateTime.now()) ,

    info.getDailySeq(),
    info.getTxnDate()  ,
    request_id 

);


    TranscationSave(trans, conn);



conn.commit();


}
catch ( SQLException e)
{

System.out.println( " The error is :: " + e );


  conn.rollback();


  return "Transaction failed";


}

    }

    catch ( Exception e  )
    {

 e.printStackTrace();
    return "Transaction failed due to system error";


    }



    return "Payment succesful";

 }

  public   Boolean debit( String accountId   , double amount ,  Connection conn ) throws SQLException 

{

String sql = " UPDATE balances  set balance  = balance - ?  where account_id = ?   ;   ";

  try ( PreparedStatement ps = conn.prepareStatement ( sql ))
  {
    ps.setDouble ( 1, amount);

    ps.setString ( 2,  accountId );

   if (  ps.executeUpdate()   !=  1   )
   {
return false ;

   }


    return true ;
  }

  catch (SQLException e  )

{

  System.out.println("The error is ::  " + e );


return false ;

}

}



  public   Boolean credit ( String accountId   , double amount ,  Connection conn ) throws SQLException 

{

String sql = " UPDATE balances  set balance  = balance +  ?  where account_id = ?   ;   ";

  try ( PreparedStatement ps = conn.prepareStatement ( sql ))
  {
    ps.setDouble ( 1, amount);

    ps.setString ( 2,  accountId );


   if (  ps.executeUpdate()   !=  1   )
   {
return false ;

   }



    return true ;
  }

  catch (SQLException e  )

{

  System.out.println("The error is ::  " + e );


return false ;

}

}


public TransactionIdInfo generateTransactionId(Connection conn) {

    LocalDate today = LocalDate.now();
    long nextSeq = 1;

    String getMaxQuery =
        "SELECT COALESCE(MAX(daily_seq), 0) FROM transactions WHERE txn_date = ?";

    try (PreparedStatement ps = conn.prepareStatement(getMaxQuery)) {

        ps.setDate(1, java.sql.Date.valueOf(today));

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                nextSeq = rs.getLong(1) + 1;
            }
        }

        String formattedDate = today.format(DateTimeFormatter.BASIC_ISO_DATE);
        String formattedSeq = String.format("%06d", nextSeq);

        String txnId = "TXN" + formattedDate + "-" + formattedSeq;

        return new TransactionIdInfo(
            txnId,
            nextSeq,
            java.sql.Date.valueOf(today)
        );

    } catch (SQLException e) {
        System.out.println("Error: " + e);
        return null;
    }
}



public void TranscationSave(Transaction trans, Connection conn)  throws SQLException 
 {

    String sql = "INSERT INTO transactions " +
                 "(txn_id, from_account, to_account, amount, status, " +
                 "from_username, to_username, daily_seq, txn_date , request_id) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ? , ? )";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, trans.getTxn_id());
        ps.setString(2, trans.getFrom_account());
        ps.setString(3, trans.getTo_account());
        ps.setDouble(4, trans.getAmount());

        // ⚠️ status must be set (IMPORTANT)
        ps.setString(5, "SUCCESS");

        ps.setString(6, trans.getFrom_username());
        ps.setString(7, trans.getTo_username());

        ps.setLong(8, trans.getDaily_seq());
        ps.setDate(9, trans.getTxn_date());

        ps.setString ( 10 , trans.getRequestId());// 
        ps.executeUpdate();

        System.out.println("Transaction saved successfully");

    } 

    
}



}



