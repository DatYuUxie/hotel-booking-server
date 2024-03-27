package com.lakesidehotel.hotelbooking.security.jwt;

import com.lakesidehotel.hotelbooking.security.user.HotelUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${security.jwt.secret}")
    private  String jwtSecret;
    @Value("${security.jwt.expirationInMils}")
    private int jwtExpirationTime;

    public String generateJwtTokenForUser(Authentication authentication){
        HotelUserDetails userPrincipal = (HotelUserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles",roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+jwtExpirationTime))
                .signWith(key(), SignatureAlgorithm.ES256).compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }


    public String getUserNameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJwt(token).getBody().getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        }
        catch (MalformedJwtException exception){
            logger.error("Invalid jwt token: {}",exception.getMessage());
        }
        catch (ExpiredJwtException exception){
            logger.error("Expired token: {}",exception.getMessage());
        }
        catch (UnsupportedJwtException exception){
            logger.error("This token is not supported: {}",exception.getMessage());
        }
        catch (IllegalArgumentException exception){
            logger.error("No claims found: {}",exception.getMessage());
        }
        return false;
    }
}
