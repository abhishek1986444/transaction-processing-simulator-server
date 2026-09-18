package com.example.code.version1.dto ;

public class OtpEntry{

	private final String otp ;

	private final long expiryTime ;

	public OtpEntry(  String otp , long expiryTime )
	{
		this.otp = otp ;
		this.expiryTime = expiryTime ;

	}

	public String getOtp () 
	{
		return otp ;
	}

	public long getExpirtyTime () 
	{
		return expiryTime ;

	}

}