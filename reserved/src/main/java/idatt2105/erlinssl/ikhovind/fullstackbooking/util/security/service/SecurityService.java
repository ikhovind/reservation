package idatt2105.erlinssl.ikhovind.fullstackbooking.util.security.service;

import idatt2105.erlinssl.ikhovind.fullstackbooking.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Date;
import java.util.Properties;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

@Slf4j
@Service
public class SecurityService {

    private static String secretKey;

    private static String setSecretKey() throws IOException {
        Properties prop = new Properties();
        InputStream input = SecurityService.class.getClassLoader().getResourceAsStream("application.properties");
        prop.load(input);
        assert input != null;
        input.close();
        return prop.getProperty("secretKey");
    }

    static {
        try {
            secretKey = setSecretKey();
        } catch (IOException e) {
            log.info("Could not set secretKey");
        }
    }

    public String createToken(String subject, long ttlMillis) {
        if (ttlMillis <= 0) {
            throw new RuntimeException("Expiry time must be greater than zero:[" + ttlMillis + "] ");
        }
        // The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder()
                .setSubject(subject)
                .signWith(signatureAlgorithm, signingKey);
        long nowMillis = System.currentTimeMillis();
        builder.setExpiration(new Date(nowMillis + ttlMillis));
        log.info("JWT Token created for user ["+subject+"]");
        return builder.compact();
    }

    String getSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(token).getBody();
        if(claims == null || claims.getSubject() == null) {
            return null;
        }
        return claims.getSubject();
    }

    public String[] getUserPartsByToken(String token) {
        String subject = getSubject(token);
        if(subject == null){
            return null;
        }
        if(subject.split("=").length != 2){
            return null;
        }
        return subject.split("=");
    }

    public String getSecretKey() {
        return secretKey;
    }
}
