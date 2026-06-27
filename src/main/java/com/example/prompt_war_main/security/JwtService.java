package com.example.prompt_war_main.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String username) {
        return generateTokenWithExpiry(username, 3600);
    }

    public String generateTokenWithExpiry(String username, long expiresInSeconds) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            long iat = System.currentTimeMillis() / 1000;
            long exp = iat + expiresInSeconds;
            String payload = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}", username, iat, exp);

            String encodedHeader = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
            String encodedPayload = base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8));

            String signatureInput = encodedHeader + "." + encodedPayload;
            String signature = hmacSha256(signatureInput, secret);

            return signatureInput + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        if (token == null) return false;
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String signatureInput = parts[0] + "." + parts[1];
            String expectedSignature = hmacSha256(signatureInput, secret);

            // Verify signature
            if (!expectedSignature.equals(parts[2])) {
                return false;
            }

            // Verify expiration
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            if (payload.contains("\"exp\":")) {
                int expIndex = payload.indexOf("\"exp\":") + 6;
                int endIndex = payload.indexOf("}", expIndex);
                if (endIndex == -1) endIndex = payload.indexOf(",", expIndex);
                String expStr = payload.substring(expIndex, endIndex).trim();
                long exp = Long.parseLong(expStr);
                if (exp < System.currentTimeMillis() / 1000) {
                    return false; // Expired
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hmacSha256(String data, String secretKeyStr) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyStr.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(hmacBytes);
    }
}
