package com.example.code.version1.dto;


public class User {

	String username ;

	String name ;

	String loginpassword ;

	String  transactionpassword ;

	Role role ;

	boolean suspended ;

	String email ;


public User ()
{
	role = Role.USER ;
	suspended = false ;

}

public User (String username , String  name  , String email  , Role role , boolean suspended  , 	String loginpassword ,

	String  transactionpassword )

{

this.username = username ;

this.name = name ;

this.role = role ;

this.suspended = suspended ;


this.email  = email ;


this.transactionpassword = transactionpassword ;

this.loginpassword = loginpassword ;

}


public String getEmail ( )
{
	return this.email ;


}

public  String  getUsername ( )
{
	return this.username ;

}


public  String  getName ( )
{
	return this.name ;
	
}



public  String  getLoginPassword ( )
{
	return this.loginpassword ;
	
}

public String getTransactionPassword ( )

{
	return this.transactionpassword ;

}

public Role getRole ( )

{

return this.role ;

}


public boolean getSuspendedStatus ( )

{

	return this.suspended ;

}

public void setSuspendedStatus ( boolean suspended )

{

this.suspended  = suspended ;

}




public void  setRole ( Role role )
{
	this.role = role ;

}


public void setUsername ( String username )
{
	this.username = username ;

}


public void setEmail ( String email )
{
	this.email = email ;

}



public void setLoginPassword ( String loginpassword )
{


this.loginpassword = loginpassword ;

}

public void setTransactionPassword ( String transactionpassword )
{

	this.transactionpassword = transactionpassword ;


}



public void setName ( String name )
{
	this.name = name ;
	
}


}
