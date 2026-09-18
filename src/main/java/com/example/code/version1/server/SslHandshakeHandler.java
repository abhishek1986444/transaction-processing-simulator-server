package com.example.code.version1.server ;


import io.netty.channel.*;
import io.netty.handler.ssl.SslHandshakeCompletionEvent ;


public class SslHandshakeHandler  extends ChannelInboundHandlerAdapter 

 {

@Override public void userEventTriggered ( ChannelHandlerContext ctx , Object evt )
{
	if ( evt instanceof SslHandshakeCompletionEvent event )
	{


	if ( event.isSuccess() )
	{
System.out.println("SSL Handshake SUCCESS");
	}

	else {

		System.out.println("SSL Handshake Failed : " +  event.cause()) ;

		ctx.close();

	}

}
}

}