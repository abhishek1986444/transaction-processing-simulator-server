package  com.example ;

import com.example.code.version1.server.*;

import  com.example.configservice.*;

public class Main {


	public static void main ( String [] args )
	{

	try {

	NettyServer object  = new NettyServer() ;

	object.start( Integer.parseInt (  ConfigService.getApp("server.port") )  ) ;


}

catch ( Exception e )
{

e.printStackTrace ();

}

finally {



}
}

}