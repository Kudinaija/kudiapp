package com.kudiapp.kudiapp.utills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for encrypting and decrypting sensitive credentials.
 * Uses AES encryption to secure credential data.
 */
@Component
@Slf4j
public class CredentialEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    @Value("${app.credential.encryption.key:KudiApp2025SecKey}")
    private String encryptionKey;

    /**
     * Encrypts the given credential text
     * 
     * @param credential The plain text credential to encrypt
     * @return Base64 encoded encrypted credential
     */
    public String encrypt(String credential) {
        if (credential == null || credential.trim().isEmpty()) {
            return null;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                    getKeyBytes(), 
                    ALGORITHM
            );
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(
                    credential.getBytes(StandardCharsets.UTF_8)
            );
            
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
            log.debug("Successfully encrypted credential");
            
            return encrypted;
            
        } catch (Exception e) {
            log.error("Failed to encrypt credential: {}", e.getMessage(), e);
            throw new RuntimeException("Credential encryption failed", e);
        }
    }

    /**
     * Decrypts the given encrypted credential
     * 
     * @param encryptedCredential The Base64 encoded encrypted credential
     * @return Plain text credential
     */
    public String decrypt(String encryptedCredential) {
        if (encryptedCredential == null || encryptedCredential.trim().isEmpty()) {
            return null;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                    getKeyBytes(), 
                    ALGORITHM
            );
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decryptedBytes = cipher.doFinal(
                    Base64.getDecoder().decode(encryptedCredential)
            );
            
            String decrypted = new String(decryptedBytes, StandardCharsets.UTF_8);
            log.debug("Successfully decrypted credential");
            
            return decrypted;
            
        } catch (Exception e) {
            log.error("Failed to decrypt credential: {}", e.getMessage(), e);
            throw new RuntimeException("Credential decryption failed", e);
        }
    }

    /**
     * Get encryption key bytes, ensuring it's 16 bytes for AES-128
     */
    private byte[] getKeyBytes() {
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[16]; // AES-128 requires 16 bytes
        
        System.arraycopy(
                keyBytes, 
                0, 
                result, 
                0, 
                Math.min(keyBytes.length, 16)
        );
        
        return result;
    }

    /**
     * Mask credential for logging/display (shows only first 3 characters)
     */
    public static String maskCredential(String credential) {
        if (credential == null || credential.length() <= 3) {
            return "***";
        }
        return credential.substring(0, 3) + "***";
    }
}