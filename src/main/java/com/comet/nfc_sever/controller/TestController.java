package com.comet.nfc_sever.controller;

import com.comet.nfc_sever.service.NfcUserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    NfcUserService service;

    @GetMapping("/mdm/{uuid}/{value}")
    public void mdm(@PathVariable(value = "uuid") String uuid, @PathVariable(value = "value") boolean value) {
        service.executeMDM(UUID.fromString(uuid), value);

    }
}
