package jku.win.se.assignmentManager.backend.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class WebtokenUtils {
	
	private static final int jwtExpirationInMs = 7200000;
	private static final String SEMA = "dUP+DRBWDJwX+OybAX2I8uWEsYPPMH2w";
	
	public static String createWebtoken(String username) {
		Map<String, Object> claims = new HashMap<>();

		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, SEMA).compact();
	}
	
	public static boolean isTokenValid(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SEMA).parseClaimsJws(token);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        } catch (ExpiredJwtException ex) {
        	return false;
        }
        return true;
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SEMA).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

}
