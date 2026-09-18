package com.example.code.version1.finalmodule ;


import  com.example.code.version1.dto.*;

import java.util.concurrent.BlockingQueue ;

import java.util.concurrent.LinkedBlockingQueue ;


public class PaymentQueueManager {


private BlockingQueue < TransferRequest > paymentQueue = 

new LinkedBlockingQueue<>();


public void submit ( TransferRequest request )
{


try {

	paymentQueue.put( request);


}

catch ( InterruptedException e )
{
	Thread.currentThread().interrupt();

}
}


public TransferRequest take() throws InterruptedException {

	return paymentQueue.take();

}

}