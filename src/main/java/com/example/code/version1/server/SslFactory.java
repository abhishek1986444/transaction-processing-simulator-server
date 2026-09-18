package com.example.code.version1.server ;


import java.io.File ;
import java.io.IOException ;
import io.netty.handler.ssl.*;


public class SslFactory {
	

	public static SslContext create() throws Exception {
	

	SslContext sslContext = SslContextBuilder 
	.forServer( new File ( "cert.pem" ) , new File ( "private.key")).build();

return sslContext ;


	}


}