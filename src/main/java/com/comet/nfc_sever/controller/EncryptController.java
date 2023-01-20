package com.comet.nfc_sever.controller;

import com.comet.nfc_sever.response.WebResponse;
import com.comet.nfc_sever.service.EncryptService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class EncryptController {

    private EncryptService service;

    @GetMapping("/encrypt")
    public ResponseEntity<WebResponse<String>> encrypt() {
        return new ResponseEntity<>(new WebResponse<>("OK", service.getPublicKey()), HttpStatus.OK);
    }
}
