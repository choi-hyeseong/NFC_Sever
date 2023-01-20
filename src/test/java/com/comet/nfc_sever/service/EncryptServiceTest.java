package com.comet.nfc_sever.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
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
}