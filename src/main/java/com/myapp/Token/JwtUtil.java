package com.myapp.Token;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.security.Key;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	 
	
	// 🔐 MUST be at least 32 chars for HS256
	private static final String SECRET = "iuhd7834yfhewwwwwwwwwwwww89008i0-90wip9iif9-0w9rf-090-wodspo-ejwdlkjkhdkjhdfkjadlf";

	private static final long ACCESS_EXP = 1000 * 60 * 15; // 15 minutes

	private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

	/* ===================== TOKEN CREATION ===================== */

	public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {

		List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();

		return Jwts.builder().setSubject(username).claim("roles", roles).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXP))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	/* ===================== TOKEN PARSING ===================== */

	public boolean isValid(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public List<GrantedAuthority> extractAuthorities(String token) {

		Claims claims = extractAllClaims(token);

		List<String> roles = claims.get("roles", List.class);

		if (roles == null)
			return List.of();

		List<GrantedAuthority> authorities = roles.stream()
				.map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role)).toList();
		return authorities;
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}
	
	



}