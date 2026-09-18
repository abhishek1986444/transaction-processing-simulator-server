package com.example.code.version1.finalmodule ;


import com.example.code.version1.dto.* ;
import java.sql.*;


public class PaymentServiceNetworkThread {

private static final PaymentQueueManager queueManager =  new PaymentQueueManager  () ;

Thread worker ;

public PaymentServiceNetworkThread () 

{
	worker = new Thread 

( new PaymentProcessor( queueManager ));


worker.setDaemon(true);

worker.setName ( "Payment-Processor");

}


public void  start()
{

worker.start() ;

}


public  void terminate()
{

worker.interrupt();

try {

	
	worker.join();
}

catch ( InterruptedException e )
{
	Thread.currentThread().interrupt() ;

}

}

public static void submitPayment ( TransferRequest request )

{
	queueManager.submit ( request );
	
}


}

