package com.example.code.version1.server ;

import javax.net.ssl.SSLHandshakeException;


import io.netty.bootstrap.ServerBootstrap ;

import io.netty.channel.*;

import io.netty.channel.nio.NioEventLoopGroup ;

import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.ssl.SslContext ;

import com.example.code.version1.finalmodule.*;

public class NettyServer {


public void start ( int port ) throws Exception {


PaymentServiceNetworkThread payservice = new PaymentServiceNetworkThread () ;

payservice.start() ;

	SslContext sslContext = SslFactory.create();

	EventLoopGroup boss = new NioEventLoopGroup();

EventLoopGroup worker = new NioEventLoopGroup();

try {

	ServerBootstrap bootstrap = new ServerBootstrap ( );

	bootstrap.group ( boss, worker )
	.channel( NioServerSocketChannel.class)
	.childHandler ( new ServerInitializer ( sslContext , payservice)) ;

       
       Channel ch = bootstrap.bind ( port).sync().channel();

       System.out.println("Https server running on port number :: " + port) ;


       ch.closeFuture().sync();


}

catch ( Exception   e )
{
throw new  RuntimeException ( "The error is : : " , e ) ;

}

finally {

boss.shutdownGracefully();

worker.shutdownGracefully();


payservice.terminate() ;

}

}

}