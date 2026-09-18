package com.example.code.version1.security;    


import com.example.configservice.*;

import  com.example.code.version1.dto.*;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys ;

import java.security.Key ;

import java.util.Date ;

import java.nio.charset.StandardCharsets ;



public class JwtService {

private static final String SECRET  = ConfigService.getSecurity("jwt.secretkey");


private static final long  EXPIRATION_MIN = 1000 * 60 * Integer.parseInt(ConfigService.getApp("jwt.expirationtime") ); // expiration time in minutes

private static final Key key;


static 
{

if ( SECRET == null   || SECRET.length() < 32)
{
      throw new IllegalArgumentException ( "JWT secret key is invalid");


}

 key = Keys
 .hmacShaKeyFor (SECRET
             .getBytes(StandardCharsets.UTF_8));


}

public static  String generateToken(String username , Role role )
{


String str  ;

str = Jwts.builder()
      .setSubject(username)
      .claim("role" , role.name())
      .setIssuedAt(new Date ())
      .setExpiration( new Date ( System.currentTimeMillis() +  EXPIRATION_MIN ))
      .signWith(key , SignatureAlgorithm.HS256)
      .compact();



return str ;

}


public static  boolean validateToken ( String token)


{


             if ( token == null || token.isBlank())
            {
                  return false ;
            }


      try {

     

     Jwts.parserBuilder()
              .setSigningKey(key)
              .build()
              .parseClaimsJws(token);
             

   
   return true ;

      }

      catch ( JwtException | IllegalArgumentException  e )
      {
            // invalid signature

            // expired token 

            // malformed token

            // empty token 

            return false ;


      }
}

public  static Claims extractClaims ( String token )
{

      if ( token == null || token.isBlank())
{
      return null ;
}



      try {




Claims claim ;

claim =  Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();


return claim ;


      }

      catch ( JwtException | IllegalArgumentException e )
      {
            return null ;

      }
}


public  static boolean isExpired ( String token )
{

    

      Claims claims = extractClaims(token);

if ( claims == null )
{
      return true ;
}

return claims.getExpiration ( ).before ( new Date());



    }  

  
}

