package com.iemdb;



import com.auth0.jwt.algorithms.Algorithm;
import com.iemdb.utils.Util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.math.*;
import java.nio.charset.*;
import java.security.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Jwts;

import com.auth0.jwt.interfaces.*;
import com.auth0.jwt.*;

import javax.crypto.spec.SecretKeySpec;

@SpringBootApplication
@ServletComponentScan
@EnableSwagger2
public class IEMDBApp {
	public static void main(String[] args) {
		SpringApplication.run(IEMDBApp.class, args);

//		SignatureAlgorithm alg = SignatureAlgorithm.HS256;
//		String key = "iemdb1401";
//		Key signKey = new SecretKeySpec(Util.getSHA(key), alg.getJcaName());
//		Instant now = Instant.now();
//
//		JwtBuilder jwtBuilder = Jwts.builder()
//				.claim("userEmail", "aaaaa")
//				.setId(UUID.randomUUID().toString())
//				.setIssuer("IEMDB")
//				.setIssuedAt(Date.from(now))
//				.setExpiration(Date.from(now.plus(24L, ChronoUnit.HOURS)))
//				.signWith(signKey, alg);
//
//		String token = jwtBuilder.compact();
//
//		token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyRW1haWwiOiJhYWFhYSIsImp0aSI6ImE0YmQzOTlmLWRlMzgtNDFkYS1hYmZkLTUxNzQ5YjdmZmVjZSIsImlzcyI6IklFTURCIiwiaWF0IjoxNjUzMDQwNDEwLCJleHAiOjE2NTMxMjY4MTB9.hT8lkIzXuV4Sh7cIgtIojJAbiqW5ClHRZrB3KCjZaHA";
//
//		Claims claims = Jwts.parser()
//				.setSigningKey(signKey)
//				.parseClaimsJws(token).getBody();
//
//		System.out.println(token);

	}
}
