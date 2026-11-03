package com.eazybytes.jobportal.security.util;

import com.eazybytes.jobportal.constants.ApplicationConstants;
import com.eazybytes.jobportal.entity.JobPortalUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@PropertySource(value="classpath:jwt.properties", ignoreResourceNotFound = true)
public class JWTUtil {

    private final Environment env;

    @Value("${jwt.issuer:Job Portal}")
    private String jwtIssuer;

    @Value("${jwt.subject:JWT Token}")
    private String jwtSubject;

    @Value("${jwt.expiration.hours:1}")
    private int  jwtExpirationHours;


    @Value("${jwt.prod.expiration.hours:1}")
    private int  jwtProdExpirationHours;

    public String generateJwtToken(Authentication authentication) {

        String jwtToken;

        // This is not related with this method, just demo of environment variable usage  Lesson 204
        //        String ttlTime = env.getProperty("cache.jobs.ttl-minutes", "5");

        int expirationHours = jwtExpirationHours;

        List<String> profiles = Arrays.asList(env.getActiveProfiles());

        if (profiles.contains("prod")) {
            expirationHours = jwtProdExpirationHours;
        }

        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        var fetchedUser = (JobPortalUser) authentication.getPrincipal();

//        jwtToken = Jwts.builder().issuer("Job Portal").subject("JWT Token")
        jwtToken = Jwts.builder().issuer(jwtIssuer).subject(jwtSubject)
                .claim("username", fetchedUser.getName())
                .claim("email", fetchedUser.getEmail())
                .claim("mobilenumber", fetchedUser.getMobileNumber())
                .claim("roles", authentication.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
//                .expiration(new java.util.Date((new java.util.Date()).getTime() + 24 * 60 * 60 * 1000))
//                .expiration(new java.util.Date((new java.util.Date()).getTime() + jwtExpirationHours * 60 * 60 * 1000))
                .expiration(new java.util.Date((new java.util.Date()).getTime() + expirationHours * 60 * 60 * 1000))
                .signWith(secretKey).compact();

        return jwtToken;

    }

    /*
    public String generateJwtToken(Authentication authentication) {

        String jwtToken;

        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        var fetchedUser = (User) authentication.getPrincipal();

        jwtToken = Jwts.builder().issuer("Job Portal").subject("JWT Token")
                .claim("username", fetchedUser.getUsername())
                .claim("roles", authentication.getAuthorities().stream().map(
                        GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + 24 * 60 * 60 * 1000))
                .signWith(secretKey).compact();

        return jwtToken;

    }
    */






}
