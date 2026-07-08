package com.LabResourceUtilizationPlatform.Security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    private static final Logger logger  = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-secret}")
    private String jwtRefreshSecret;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);

        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateAccessToken(UserDetails userDetails){
        String username = userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(accessKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .signWith(refreshKey())
                .compact();
    }

    public String getUsernameFromAccessToken(String token){
        return Jwts.parser()
                .verifyWith(accessKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(refreshKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateAccessToken(String token){
        try{
            logger.debug("Validating access token");
            Jwts.parser()
                    .verifyWith(accessKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", token);
        }catch (ExpiredJwtException e){
            logger.error("JWT token is expired: {}", token);
        }catch(UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}",token);
        }catch(IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}",token);
        }
        return false;
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(refreshKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (MalformedJwtException e) {
            logger.error("Invalid refresh token");
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported refresh token");
        } catch (IllegalArgumentException e) {
            logger.error("Refresh token is empty");
        }
        return false;
    }


    private SecretKey accessKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private SecretKey refreshKey() {
        return Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes());
    }


}
