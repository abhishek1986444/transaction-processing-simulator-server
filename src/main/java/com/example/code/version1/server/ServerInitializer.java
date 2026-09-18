package com.example.code.version1.server;

import io.netty.channel.*;

import io.netty.channel.socket.SocketChannel;

import io.netty.handler.ssl.SslContext ;

import io.netty.handler.codec.http.*;


import com.example.code.version1.finalmodule.*;


public class ServerInitializer  extends ChannelInitializer <SocketChannel>

{

private final PaymentServiceNetworkThread paymentService ;


private final SslContext  sslContext ;

public ServerInitializer ( SslContext sslContext ,


 PaymentServiceNetworkThread paymentService )

{
	this.sslContext = sslContext ;
	this.paymentService = paymentService ;

}



@Override protected void  initChannel ( SocketChannel ch)
{
	ch.pipeline()

	.addLast ("ssl" , sslContext.newHandler(ch.alloc()))

	.addLast ("sslHandler" , new SslHandshakeHandler())

.addLast(new ChannelInboundHandlerAdapter() {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof javax.net.ssl.SSLHandshakeException) {
            System.out.println("SSL rejected client");
            ctx.close();
            return;
        }

        ctx.fireExceptionCaught(cause);
    }
})

	.addLast ( "http" , new HttpServerCodec())

	.addLast ( "aggregator" , new HttpObjectAggregator ( 65536))

	.addLast ("router" , new SimpleHttpRouter(paymentService ))
	.addLast("globalError", new GlobalExceptionHandler());

}


}