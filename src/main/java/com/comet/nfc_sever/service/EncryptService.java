package com.comet.nfc_sever.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.security.*;
import java.util.Base64;

@Service
public class EncryptService {

    private KeyPair keyPair;

    private void initKeyPair() {
        try {
            SecureRandom random = new SecureRandom();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048, random);
            keyPair = generator.genKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    protected String encrypt(String input) {
        init();
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encryptByte = cipher.doFinal(input.getBytes(Charset.defaultCharset()));
            return Base64.getUrlEncoder().encodeToString(encryptByte); //Base64.getEncoder().encodeToString() 사용시 + 문자를 공백으로 치환해버림..
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected String decrypt(String input) {
        init();
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decodeByte = Base64.getUrlDecoder().decode(input);
            byte[] decryptByte = cipher.doFinal(decodeByte);
            return new String(decryptByte);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPublicKey() {
        init();
        return Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    private void init() {
        if (keyPair == null)
            initKeyPair();
    }
}
