package com.example.code.version1.server ;


import  com.example.code.version1.dto.* ;

import  com.example.code.version1.security.*;

import com.example.code.version1.authandextraction.* ;

import  com.example.code.version1.finalmodule.* ;


import com.example.code.version1.communication.*;


import javax.net.ssl.SSLHandshakeException;


import io.netty.channel.*;


import io.netty.handler.codec.http.*;

import io.netty.buffer.Unpooled ;

import io.netty.util.CharsetUtil ;


import java.util.concurrent.ConcurrentHashMap ;

import java.util.Map ;

import java.util.HashMap ;

import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import io.jsonwebtoken.*;

import io.jsonwebtoken.security.*;


import java.time.LocalTime ;

import java.time.LocalDate ;

import java.util.UUID ;


import java.sql.*;



public class SimpleHttpRouter extends

SimpleChannelInboundHandler <FullHttpRequest>


 {

    private final PaymentServiceNetworkThread  paymentService ;

    public SimpleHttpRouter ( PaymentServiceNetworkThread  paymentService )
    {
        this.paymentService = paymentService ;


    }

private static final ConcurrentHashMap < String , OtpEntry > otpByUser   = new ConcurrentHashMap<>(); 



private static final ConcurrentHashMap < String , String > userNameToToken   = new ConcurrentHashMap<>(); 

 private static final ConcurrentHashMap < String , String > removedtokens

  = new ConcurrentHashMap<>() ;



 private static final ConcurrentHashMap < String , User >
 
 dummyusers = new ConcurrentHashMap <> ();



static {

    try {
        UserLoaderClass.userLoader(dummyusers, DBUtil1.getConnection());
    } catch (SQLException  | ClassNotFoundException e) {
        e.printStackTrace();

        // Optionally stop the application
        throw new RuntimeException("Failed to load users during startup", e);
    }


}


@Override protected void channelRead0 ( ChannelHandlerContext ctx

, FullHttpRequest req 
)

{
	
String uri = req.uri().split("\\?")[0];

	if ("/health".equals(uri))
	{
		send ( ctx , HttpResponseStatus.OK , "health ok ") ;
         
         return ;
	}


	try {

     switch  ( uri )
     {

     case "/login" ->  handleUserLogin ( ctx , req);

     case "/login/verify-otp" ->  handleOtpVerify ( ctx , req);



  case "/login/status" ->  handleloginstatus(ctx , req );

    case "/logout" ->  logout(ctx , req );


case "/admin/users/status"  ->
    {
            System.out.println("Not implemented yet");

    }

 case "/admin/users/action" -> removeUser(ctx , req ) ;

case  "/payment/pay" -> pay ( ctx, req) ;

case "/payment/status" -> jobChecker(ctx, req);


     default -> send ( ctx  , HttpResponseStatus.NOT_FOUND , "not found") ;


     }
	} finally {

	}

}

private void pay(
        ChannelHandlerContext ctx,
        FullHttpRequest req)
{
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode response = mapper.createObjectNode();

    try
    {
        String authHeader =
                req.headers().get(
                        HttpHeaderNames.AUTHORIZATION);

        String token =
                JwtExtractorService
                        .jwtExtactormethod(authHeader);

        if (token == null)
        {
            response.put("response", "failed");
            response.put("message",
                    "Missing or invalid bearer token");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        Claims claims =
                JwtService.extractClaims(token);

        if (claims == null)
        {
            response.put("response", "failed");
            response.put("message",
                    "invalid token");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String tokenUsername =
                claims.getSubject();

        if (tokenUsername == null)
        {
            response.put("response", "failed");
            response.put("message",
                    "invalid token payload");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String activeToken =
                userNameToToken.get(tokenUsername);

        if (activeToken == null)
        {
            response.put("response", "failed");
            response.put("message",
                    "user not logged in");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        if (!activeToken.equals(token))
        {
            response.put("response", "failed");
            response.put("message",
                    "session invalid");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String body =
                req.content()
                        .toString(CharsetUtil.UTF_8);

        TransferRequest transferRequest =
                mapper.readValue(
                        body,
                        TransferRequest.class);

        if (!tokenUsername.equals(
                transferRequest.getFromUsername()))
        {
            response.put("response", "failed");
            response.put("message",
                    "username mismatch");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String requestId =
                LocalDate.now()
                        + "-"
                        + LocalTime.now()
                        + "-"
                        + UUID.randomUUID();

        transferRequest.setRequestId(
                requestId);

        transferRequest.setStatus(
                "PENDING");


    PaymentServiceNetworkThread.submitPayment( transferRequest );


        System.out.println(
                "Payment Request Received");

        System.out.println(
                "From User : "
                        + transferRequest.getFromUsername());

        System.out.println(
                "To User : "
                        + transferRequest.getToUsername());

        System.out.println(
                "Amount : "
                        + transferRequest.getAmount());

        response.put("response",
                "success");

        response.put("requestId",
                requestId);

        response.put("status",
                transferRequest.getStatus());

        sendJson(
                ctx,
                HttpResponseStatus.OK,
                mapper.writeValueAsString(response));
    }
    catch (Exception e)
    {

System.err.println("The error is ::  " + e );


        try
        {
            response.put("response",
                    "failed");

            response.put("message",
                    "invalid request");

            sendJson(
                    ctx,
                    HttpResponseStatus.BAD_REQUEST,
                    mapper.writeValueAsString(response));
        }
        catch (Exception ignored)
        {
        }
    }
}


private void send ( ChannelHandlerContext ctx , HttpResponseStatus status  , String msg )
{

	FullHttpResponse response = new DefaultFullHttpResponse ( 

HttpVersion.HTTP_1_1 , 
status , 
Unpooled.copiedBuffer ( msg , CharsetUtil.UTF_8 ) 

	);

response.headers().set ( HttpHeaderNames.CONTENT_TYPE , "text/plain");

response.headers().set ( HttpHeaderNames.CONTENT_LENGTH , response.content().readableBytes());

ctx.writeAndFlush(response);


}



private void sendJson ( ChannelHandlerContext ctx , HttpResponseStatus status  , String msg )
{

	FullHttpResponse response = new DefaultFullHttpResponse ( 

HttpVersion.HTTP_1_1 , 
status , 
Unpooled.copiedBuffer ( msg , CharsetUtil.UTF_8 ) 

	);

response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

response.headers().set ( HttpHeaderNames.CONTENT_LENGTH , response.content().readableBytes());

ctx.writeAndFlush(response);


}



private void insertpayment ( ChannelHandlerContext ctx , FullHttpRequest req )
{

   Boolean checker = handleloginstatus(ctx , req );


if ( checker == false )
{
    return ;

}

}

private void logout(
        ChannelHandlerContext ctx,
        FullHttpRequest request) {

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode response = mapper.createObjectNode();

    try {

        String authHeader =
                request.headers().get(
                        HttpHeaderNames.AUTHORIZATION);

        String token =
                JwtExtractorService.jwtExtactormethod(authHeader);

        if (token == null) {

            response.put("response", "failed");
            response.put("message",
                    "Missing or invalid bearer token");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        Claims claim = JwtService.extractClaims(token);

        if (claim == null) {

            response.put("response", "failed");
            response.put("message", "invalid token");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String tokenUsername = claim.getSubject();

        if (tokenUsername == null) {

            response.put("response", "failed");
            response.put("message",
                    "Invalid token payload");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String requestUserName =
                request.headers().get("username");

        if (requestUserName == null) {

            response.put("response", "failed");
            response.put("message",
                    "missing username in request");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        if (!tokenUsername.equals(requestUserName)) {

            response.put("response", "failed");
            response.put("message",
                    "username mismatch");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String storedToken =
                userNameToToken.get(tokenUsername);

        if (storedToken == null) {

            response.put("response", "failed");
            response.put("message",
                    "user already logged out");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        if (!storedToken.equals(token)) {

            response.put("response", "failed");
            response.put("message",
                    "session token mismatch");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }


        userNameToToken.remove(tokenUsername);


        response.put("response", "success");
        response.put("message",
                "user logout successful");

        sendJson(
                ctx,
                HttpResponseStatus.OK,
                mapper.writeValueAsString(response));

    }
    catch (Exception e) {

        e.printStackTrace();

        try {

            response.put("response", "failed");
            response.put("message",
                    "internal server error");

            sendJson(
                    ctx,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    mapper.writeValueAsString(response));

        }
        catch (Exception ignored) {
        }
    }
}


private void removeUser(
        ChannelHandlerContext ctx,
        FullHttpRequest request) {

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode response = mapper.createObjectNode();

    try {

        String authHeader =
                request.headers().get(
                        HttpHeaderNames.AUTHORIZATION);

        String token =
                JwtExtractorService.jwtExtactormethod(
                        authHeader);

        if (token == null) {

            response.put("response", "failed");
            response.put("message",
                    "missing or invalid bearer token");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        Claims claims =
                JwtService.extractClaims(token);

        if (claims == null) {

            response.put("response", "failed");
            response.put("message",
                    "invalid token");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        Role role;

        try {

            role = Role.valueOf(
                    claims.get(
                            "role",
                            String.class));

        }
        catch (Exception e) {

            response.put("response", "failed");
            response.put("message",
                    "invalid role");

            sendJson(
                    ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        if (role != Role.ADMIN) {

            response.put("response", "failed");
            response.put("message",
                    "access denied");

            sendJson(
                    ctx,
                    HttpResponseStatus.FORBIDDEN,
                    mapper.writeValueAsString(response));

            return;
        }

        String targetUsername =
                request.headers().get(
                        "target-username");

        if (targetUsername == null ||
                targetUsername.isBlank()) {

            response.put("response", "failed");
            response.put("message",
                    "target username missing");

            sendJson(
                    ctx,
                    HttpResponseStatus.BAD_REQUEST,
                    mapper.writeValueAsString(response));

            return;
        }
            

            if (!dummyusers.containsKey(targetUsername)) {



            response.put("response", "failed");
            response.put("message",
                    "User doesn't exist");

            sendJson(
                    ctx,
                    HttpResponseStatus.BAD_REQUEST,
                    mapper.writeValueAsString(response));

    // user does not exist
    return;
}


        String removedToken =
                userNameToToken.remove(
                        targetUsername);

        if (removedToken == null) {

            response.put("response", "failed");
            response.put("message",
                    "user not logged in");

            sendJson(
                    ctx,
                    HttpResponseStatus.NOT_FOUND,
                    mapper.writeValueAsString(response));

            return;
        }

        response.put("response", "success");
        response.put("message",
                "user removed successfully");

        sendJson(
                ctx,
                HttpResponseStatus.OK,
                mapper.writeValueAsString(response));

    }
    catch (Exception e) {

        e.printStackTrace();

        try {

            response.put("response", "failed");
            response.put("message",
                    "internal server error");

            sendJson(
                    ctx,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    mapper.writeValueAsString(response));

        }
        catch (Exception ignored) {
        }
    }
}



private Boolean handleloginstatus ( ChannelHandlerContext ctx , FullHttpRequest request )

{


ObjectMapper mapper = new ObjectMapper() ;

     ObjectNode response  = mapper.createObjectNode();



try {
String authHeader  = request.headers()
.get(HttpHeaderNames.AUTHORIZATION);


String token = JwtExtractorService
.jwtExtactormethod(authHeader);



if ( token == null ){

  response.put("response" , "failed");

    response.put ("message" , "Missing or invalid bearer token");
      

    sendJson (ctx , HttpResponseStatus.UNAUTHORIZED,

     mapper.writeValueAsString(response));

      return  false  ;
  }

  Claims  claim = JwtService.extractClaims(token);


if (claim == null)
{
    response.put("response", "failed");
    response.put("message", "invalid token");

    sendJson(
            ctx,
            HttpResponseStatus.UNAUTHORIZED,
            mapper.writeValueAsString(response)
    );

    return false ;
}


String tokenUsername =
        claim.getSubject();

 if ( tokenUsername == null )
 {
     response.put("response" , "failed");

    response.put ("message" , "Invalid token payload");
      

    sendJson (ctx , HttpResponseStatus.UNAUTHORIZED,

     mapper.writeValueAsString(response));

      return  false  ;
 }


String requestUserName = request.headers().get("username");

if ( requestUserName == null )
{

     response.put("response" , "failed");

    response.put ("message" , "missing username in request  ");
      

    sendJson (ctx , HttpResponseStatus.UNAUTHORIZED,

     mapper.writeValueAsString(response));

      return   false ;
 
}

if (!tokenUsername.equals(requestUserName))
{
    response.put("response", "failed");
    response.put("message", "username mismatch");

    sendJson(
            ctx,
            HttpResponseStatus.UNAUTHORIZED,
            mapper.writeValueAsString(response)
    );

    return false ;
}

String activeToken =
        userNameToToken.get(tokenUsername);

if (activeToken == null) {

    response.put("response", "failed");
    response.put("message",
            "user not logged in");

    sendJson(
            ctx,
            HttpResponseStatus.UNAUTHORIZED,
            mapper.writeValueAsString(response)
    );

    return  false ;
}

if (!activeToken.equals(token)) {

    response.put("response", "failed");
    response.put("message",
            "session invalid");

    sendJson(
            ctx,
            HttpResponseStatus.UNAUTHORIZED,
            mapper.writeValueAsString(response)
    );

    return  false ;
}


response.put("response", "success");
response.put("message", "user session valid ");

sendJson(
        ctx,
        HttpResponseStatus.OK,
        mapper.writeValueAsString(response)
);



otpByUser.remove(requestUserName);



return true ;

}

catch ( Exception e )
{


System.out.println("The error is :: "  + e );

return false ;


}


}

private void handleUserLogin ( 

ChannelHandlerContext ctx ,  FullHttpRequest request)
{

String body = request.content().toString(CharsetUtil.UTF_8);

ObjectMapper mapper = new ObjectMapper ();

try{

	JsonNode node = mapper.readTree(body);

	String UserName = node.get("username").asText();

    String password = node.get("password").asText();
      
      boolean UserExists = dummyusers.containsKey(UserName);

     
     ObjectNode response  = mapper.createObjectNode();


if ( ! UserExists  )
 

{

	    response.put("response" , "failed");

     response.put ("message" , "wrong user name");
      

     	sendJson (ctx , HttpResponseStatus.NOT_FOUND ,

	 mapper.writeValueAsString(response));

      return  ;


}


if (!PasswordUtil.verify(
        password,
      dummyusers.get(UserName).getLoginPassword()) ) 


{

	    response.put("response" , "failed");

     response.put ("message" , "wrong password");
      

     	sendJson (ctx , HttpResponseStatus.NOT_FOUND ,

	 mapper.writeValueAsString(response));

      return  ;

}

    Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10)); // 0–9
        }



       long expiry = System.currentTimeMillis() + ( 1 * 60 * 1000 );

otpByUser.put (UserName , new OtpEntry(otp.toString()  , expiry  ) );


	response.put ("response" , "accepted");
	response.put ("message" , "Otp sended ");

	sendJson (ctx , HttpResponseStatus.OK,
       

	 mapper.writeValueAsString(response));

EmailService.sendOtp(    dummyusers.get ( UserName).getEmail()     ,   otp.toString()  )  ; 


}

catch ( Exception e )
{

	try {
	ObjectNode response = mapper.createObjectNode();

	response.put ("response" , "failed");
	response.put ("message" , "Invalid JSON");

	sendJson (ctx , HttpResponseStatus.NOT_FOUND ,

	 mapper.writeValueAsString(response));
  }
catch ( Exception t )
{
	t.printStackTrace ();
}

}

}


private void handleOtpVerify (  ChannelHandlerContext ctx ,  FullHttpRequest request)

{

String json = request.content()
              .toString (CharsetUtil.UTF_8);


ObjectMapper mapper = new ObjectMapper();

try {

     JsonNode node = mapper.readTree(json);

     String UserName = node.get("username").asText();

     String otp = node.get("otp").asText();


     boolean UserExists = dummyusers.containsKey (   UserName    );

     ObjectNode response = mapper.createObjectNode ();

  if ( ! UserExists  )
 

{

        response.put("response" , "failed");

     response.put ("message" , "wrong user name");
      

        sendJson (ctx , HttpResponseStatus.NOT_FOUND ,

     mapper.writeValueAsString(response));

      return  ;


}

if (  otpByUser.containsKey(UserName) )

{

if (

 System.currentTimeMillis()

 >  otpByUser.get(UserName).getExpirtyTime()

)

{
otpByUser.remove(UserName);

      response.put("response" , "failed");

     response.put ("message" , "OTP expired");
      

        sendJson (ctx , HttpResponseStatus.NOT_FOUND ,

     mapper.writeValueAsString(response));

      return  ;

}


if ( otp.equals(otpByUser.get(UserName).getOtp() ) ) 

{

  String token = JwtService.generateToken( UserName , dummyusers.get(UserName).getRole());



      response.put("response" , "success");

     response.put ("message" , token);
      

        sendJson (ctx , HttpResponseStatus.OK,

     mapper.writeValueAsString(response));

        userNameToToken.put( UserName , token );



      return  ;

}

else {

          response.put("response" , "failed");

     response.put ("message" , "Invalid OTP");
      

        sendJson (ctx , HttpResponseStatus.NOT_FOUND ,

     mapper.writeValueAsString(response));

      return  ;

}

}

else {

       response.put("response" , "failed");

     response.put ("message" , "Otp not generated ");
      

        sendJson (ctx , HttpResponseStatus.NOT_FOUND ,

     mapper.writeValueAsString(response));

      return  ;


}

}


catch ( Exception e )
{
    System.out.println("The error is  ::  " + e );

}

}

@Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    if (cause instanceof javax.net.ssl.SSLHandshakeException) {
        System.out.println("Rejected SSL client (handshake failed)");
        ctx.close();
        return;
    }

    System.out.println("Server error: " + cause);
    ctx.close();
}




private void jobChecker(
        ChannelHandlerContext ctx,
        FullHttpRequest request) {

    ObjectMapper mapper = new ObjectMapper();
    ObjectNode response = mapper.createObjectNode();

    try {

        // ===========================
        // Authorization Header
        // ===========================

        String authHeader =
                request.headers().get(HttpHeaderNames.AUTHORIZATION);

        String token =
                JwtExtractorService.jwtExtactormethod(authHeader);

        if (token == null) {

            response.put("response", "failed");
            response.put("message",
                    "Missing or invalid bearer token");

            sendJson(ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        // ===========================
        // JWT Validation
        // ===========================

        Claims claims = JwtService.extractClaims(token);

        if (claims == null) {

            response.put("response", "failed");
            response.put("message", "Invalid token");

            sendJson(ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        String tokenUsername = claims.getSubject();

        if (tokenUsername == null) {

            response.put("response", "failed");
            response.put("message", "Invalid token payload");

            sendJson(ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        // ===========================
        // Username Validation
        // ===========================

        String requestUsername =
                request.headers().get("username");

        if (requestUsername == null) {

            response.put("response", "failed");
            response.put("message",
                    "Missing username");

            sendJson(ctx,
                    HttpResponseStatus.BAD_REQUEST,
                    mapper.writeValueAsString(response));

            return;
        }

        if (!tokenUsername.equals(requestUsername)) {

            response.put("response", "failed");
            response.put("message",
                    "Username mismatch");

            sendJson(ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        // ===========================
        // Session Validation
        // ===========================

        String storedToken =
                userNameToToken.get(tokenUsername);

        if (storedToken == null) {

            response.put("response", "failed");
            response.put("message",
                    "User not logged in");

            sendJson(ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        if (!storedToken.equals(token)) {

            response.put("response", "failed");
            response.put("message",
                    "Session token mismatch");

            sendJson(ctx,
                    HttpResponseStatus.UNAUTHORIZED,
                    mapper.writeValueAsString(response));

            return;
        }

        // ===========================
        // Request Id
        // ===========================

        String requestId =
                request.headers().get("request_id");

        if (requestId == null) {

            response.put("response", "failed");
            response.put("message",
                    "Missing request_id");

            sendJson(ctx,
                    HttpResponseStatus.BAD_REQUEST,
                    mapper.writeValueAsString(response));

            return;
        }

        // ===========================
        // Search Request
        // ===========================

        RequestStatusResponse result;

        try (Connection conn = DBUtil1.getConnection()) {

            result = RequestSearchService.searchRequest(
                    conn,
                    requestId);

        }

        // ===========================
        // Response
        // ===========================

        if (!tokenUsername.equals(result.getFromUsername())) {

    response.put("response", "failed");
    response.put("message", "You are not authorized to view this transaction");

    sendJson(
            ctx,
            HttpResponseStatus.FORBIDDEN,
            mapper.writeValueAsString(response)
    );

    return;
}



        if (!result.isFound()) {

            response.put("response", "failed");
            response.put("message",
                    "Request not found");

        } else {

            response.put("response", "success");

            response.set(
                    "data",
                    mapper.valueToTree(result));
        }

        sendJson(ctx,
                HttpResponseStatus.OK,
                mapper.writeValueAsString(response));

    }

    catch (Exception e) {

        e.printStackTrace();

        try {

            response.put("response", "failed");
            response.put("message",
                    "Internal server error");

            sendJson(ctx,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    mapper.writeValueAsString(response));

        } catch (Exception ignored) {
        }

    }

}

}

