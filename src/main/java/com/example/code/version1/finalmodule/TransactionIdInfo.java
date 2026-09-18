package com.example.code.version1.finalmodule ;


import java.sql.Connection ;
import java.sql.SQLException ;
import java.sql.PreparedStatement ;
import java.sql.ResultSet ;
import java.time.LocalDate ;
import java.time.format.DateTimeFormatter ;
import java.sql.Date;


public class TransactionIdInfo {

    private String txnId;
    private long dailySeq;
    private java.sql.Date txnDate;

    public TransactionIdInfo(String txnId, long dailySeq, java.sql.Date txnDate) {
        this.txnId = txnId;
        this.dailySeq = dailySeq;
        this.txnDate = txnDate;
    }

    public String getTxnId() {
        return txnId;
    }

    public long getDailySeq() {
        return dailySeq;
    }

    public java.sql.Date getTxnDate() {
        return txnDate;
    }
}