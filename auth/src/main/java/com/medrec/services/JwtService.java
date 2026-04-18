package com.medrec.services;

import com.medrec.dtos.TokenDataDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class JwtService {
    private static JwtService instance;

    private final Logger logger = Logger.getLogger(JwtService.class.getName());

    private final String secret = System.getenv("JWT_SECRET");
    private final Key key;

    private JwtService() {
	String secret = System.getenv("JWT_SECRET");

	if (secret == null || secret.length() < 32) {
	    throw new IllegalStateException("JWT_SECRET is missing or too weak (must be >= 32 chars)");
	}

	this.key = Keys.hmac.ShaKeyFor(secret.getBytes());
    private final long tokenValidityMs = 1000 * 60 * 60;

    private JwtService() {}

    public static JwtService getInstance() {
        if (instance == null) {
            instance = new JwtService();
        }
        return instance;
    }

    public String generateToken(int id, String firstName, String lastName, String email, String role) {
        logger.info(String.format("Generating JWT token for email: %s and role: %s", email, role));

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityMs);

        Map<String, String> claims = new HashMap<>();

        claims.put("id", String.valueOf(id));
        claims.put("firstName", firstName);
        claims.put("lastName", lastName);
        claims.put("role", role);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isUserAuthorized(String token, List<String> requiredRoles) {
        if(!isTokenValid(token)) {
            return false;
        }
        TokenDataDTO tokenData = getDataFromToken(token);

        for(String requiredRole : requiredRoles) {
            if(requiredRole.equals(tokenData.getRole())) {
                return true;
            }
        }

        return false;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            this.logger.severe(e.getMessage());
            return false;
        }
    }

    public TokenDataDTO getDataFromToken(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);

        Claims claims = claimsJws.getBody();

        int id = Integer.parseInt(claims.get("id", String.class));

        return new TokenDataDTO(
            id,
            claims.get("firstName", String.class),
            claims.get("lastName", String.class),
            claims.getSubject(),
            claims.get("role", String.class)
        );
    }
}
