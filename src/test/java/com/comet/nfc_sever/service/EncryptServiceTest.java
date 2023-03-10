package com.comet.nfc_sever.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EncryptServiceTest {

    @Test
    @DisplayName("Encrypt_Test")
    public void ENCRYPT_TEST() {
        EncryptService service = new EncryptService();
        String input = "Hello World!";
        String encrypt = service.encrypt(input);
        String decrypt = service.decrypt(encrypt); //나 뭐하냐..? 왜 input을 그대로 넣어?
        System.out.println(encrypt);
        System.out.println(decrypt);
        assertEquals(input, decrypt);
    }

    @Test
    @DisplayName("AES_Encrypt_Test")
    public void AES_Encrypt_Test() {
        EncryptService service = new EncryptService();
        String input = "Hello World!";
        String key = UUID.randomUUID().toString();
        String enc = service.AESEncrypt(input, key);
        String dec = service.AESDecrypt(enc, key);
        System.out.println(enc);
        assertEquals(input, dec);
    }

}