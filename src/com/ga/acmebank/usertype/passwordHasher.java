package com.ga.acmebank.usertype;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class passwordHasher{


    public static String hash(String plainPass){
        SecureRandom random= new SecureRandom();
        byte[] hash;
        byte[] salt= new byte[16];
        random.nextBytes(salt);
        KeySpec spec= new PBEKeySpec(plainPass.toCharArray(), salt, 65536,128);
        try {
            SecretKeyFactory fact= SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
           hash = fact.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException |InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(salt)+":"+Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPasswords(String inputPassword, String storedHash){
            if (storedHash== null){
                return false;
            }
            String[] parts=storedHash.split(":");
            byte[] salt= Base64.getDecoder().decode(parts[0]);
            byte[] actualHashed= Base64.getDecoder().decode(parts[1]);

            KeySpec newSpec= new PBEKeySpec(inputPassword.toCharArray(),salt,65536,128);
            try{
                    SecretKeyFactory newFact= SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                    byte[] inputHash = newFact.generateSecret(newSpec).getEncoded();
                    return java.util.Arrays.equals(actualHashed,inputHash);
                } catch (NoSuchAlgorithmException |InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
//        return false;
    }
}
