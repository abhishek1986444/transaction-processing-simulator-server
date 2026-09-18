package com.example.code.version1.finalmodule ;

import java.sql.*;

import java.util.concurrent.*;

import java.util.*;

import com.fasterxml.jackson.databind.*;

import  com.example.code.version1.dto.* ;


public class PaymentProcessor implements Runnable  {


private final PaymentQueueManager queueManager ;


public PaymentProcessor ( PaymentQueueManager queueManager )
{
	this.queueManager = queueManager ;

}


@Override public void run ()
{

boolean on = true ;

while ( on   )
{


	try {


TransferRequest request = queueManager.take() ;

process ( request );


	}

	catch ( InterruptedException e )

{

Thread.currentThread().interrupt();

on = false ;


}

}
}


private void process ( TransferRequest request )
{


try 

{


PaymentService paymentService = new PaymentService();


   String result = paymentService.transferto(

           request.getFromUsername(),
           request.getPaymentPassword(),
           request.getToUsername(),
           request.getFromAccount(),
           request.getToAccount(),
           request.getAmount(),
           DBUtil1.getConnection(),
           request.getRequestId()

   );

   System.out.println(result);


if (  ! result.equals ( "Payment succesful"))

 {

   FailedPayment.insertFailedPayment( DBUtil1.getConnection() ,
    request.getFromUsername() , request.getRequestId () , result) ;

}
}


catch ( SQLException | ClassNotFoundException e )
{
	System.err.println("The error is from paymentprocessor  :: " + e );
}

}

}