package com.tianleyu.github;

import java.io.File;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Key;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import com.google.common.io.Files;

@Slf4j
public class Utils {
    static PrivateKey get(String filename) throws Exception {
        File directory = new File(filename);
        log.warn("\u001b[31m[com.tianleyu.github.Utils] Using key file " + directory.getAbsolutePath() + " to sign JWT\u001b[0m");
        byte[] keyBytes = Files.toByteArray(new File(filename));

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    static String createJWT(String githubAppId, long ttlMillis, String keyFile) throws Exception {
        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // We will sign our JWT with our private key
        Key signingKey = get(keyFile);

        // Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setIssuer(githubAppId)
                .signWith(signingKey, signatureAlgorithm);

        // if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }
    // public static void main(String[] args) throws Exception {
    // String jwtToken = createJWT("44435", 600000); // sdk-github-api-app-test
    // GitHub gitHubApp = new GitHubBuilder().withJwtToken(jwtToken).build();
    // }

}
