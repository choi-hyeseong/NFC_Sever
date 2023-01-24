package com.comet.nfc_sever.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.*;
import java.util.Base64;

@Service
@Slf4j
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
            log.error(e.getLocalizedMessage());
        }
    }
     public String encrypt(String input) {
        init();
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] encryptByte = cipher.doFinal(input.getBytes(Charset.defaultCharset()));
            return Base64.getUrlEncoder().encodeToString(encryptByte); //Base64.getEncoder().encodeToString() 사용시 + 문자를 공백으로 치환해버림..
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

     public String decrypt(String input) {
        if (input == null || input.equals(""))
            return null;
        init();
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] decodeByte = Base64.getUrlDecoder().decode(input);
            byte[] decryptByte = cipher.doFinal(decodeByte);
            return new String(decryptByte);
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    public String getPublicKey() {
        init();
        return Base64.getUrlEncoder().encodeToString(keyPair.getPublic().getEncoded());
    }

    //AES 는 16, 24, 32 바이트여야 암호화 가ㅡㄴㅇ
    public String AESEncrypt(String input, String key) {
        Key secretKeySpec = new SecretKeySpec(key.substring(0, 16).getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypt = cipher.doFinal(input.getBytes());
            return Base64.getUrlEncoder().encodeToString(encrypt);
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    public String AESDecrypt(String input, String key) {
        Key secretKeySpec = new SecretKeySpec(key.substring(0, 16).getBytes(), "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] urlDecode = Base64.getUrlDecoder().decode(input.getBytes());
            byte[] decrypt = cipher.doFinal(urlDecode);
            return new String(decrypt);
        }
        catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    private void init() {
        if (keyPair == null)
            initKeyPair();
    }
}
