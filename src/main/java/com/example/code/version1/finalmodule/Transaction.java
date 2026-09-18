package com.example.code.version1.finalmodule ;


import java.sql.Timestamp ;
import java.sql.Date ;


public class Transaction {

private String request_id ;

private String  txn_id;

private String from_username;
private String to_username;

private String from_account ;

private String to_account ;
                  
private double  amount ;


private Timestamp  created_at;


    private long daily_seq;
    private Date txn_date;
  
public Transaction() {
}

public Transaction(String txn_id,                 
                  String from_username , 
                  String to_username,
                   String from_account,
                   String to_account,
                   double amount,
                   Timestamp created_at ,
                   long daily_seq , 
                    Date txn_date ,

                    String request_id 

               ) {

    this.txn_id = txn_id;
    this.from_account = from_account;
    this.to_account = to_account;
    this.amount = amount;
    this.created_at = created_at;
    this.txn_date =  txn_date;
    this.daily_seq  = daily_seq ;
    this.from_username = from_username ;
    this.to_username = to_username ;

    this.request_id = request_id ;

}


  // getters + setters 

public String getTxn_id() {
    return txn_id;
}

public void setTxn_id(String txn_id) {
    this.txn_id = txn_id;
}

public String getFrom_account() {
    return from_account;
}

public void setFrom_account(String from_account) {
    this.from_account = from_account;
}

public String getTo_account() {
    return to_account;
}

public void setTo_account(String to_account) {
    this.to_account = to_account;
}

public double getAmount() {
    return amount;
}

public void setAmount(double amount) {
    this.amount = amount;
}



public Timestamp getCreated_at() {
    return created_at;
}

public void setCreated_at(Timestamp created_at) {
    this.created_at = created_at;
}

    public long getDaily_seq() {
        return daily_seq;
    }

    public void setDaily_seq(long daily_seq) {
        this.daily_seq = daily_seq;
    }

    public Date getTxn_date() {
        return txn_date;
    }

    public void setTxn_date(Date txn_date) {
        this.txn_date = txn_date;
    }


public String getFrom_username() {
    return from_username;
}

public void setFrom_username(String from_username) {
    this.from_username = from_username;
}

public String getTo_username() {
    return to_username;
}

public void setTo_username(String to_username) {
    this.to_username = to_username;
}


public void setRequestId ( String request_id )
{

this.request_id = request_id ;

}

public String getRequestId ( )
{
    return request_id ;

}

}