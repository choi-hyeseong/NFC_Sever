package com.comet.nfc_sever.controller;

import com.comet.nfc_sever.dto.MdmRequestDto;
import com.comet.nfc_sever.service.EncryptService;
import com.comet.nfc_sever.service.NfcUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mdm")
@Slf4j
public class MdmController {

    private final NfcUserService service;
    private final EncryptService encryptService;

    @Value("${nfc.server.request-timeout}")
    private long requestTimeout;

    @PostMapping("/request")
    public ResponseEntity<HttpStatus> mdm(@RequestBody MdmRequestDto dto) {
        String data = dto.getData();
        if ((data.contains("Android") || data.contains("Card")) && data.split("\\|").length == 2) {
            String[] split = data.split("\\|");
            if (data.contains("Android")) {
                log.info("!");
                String encData = split[0];
                String decrypt = encryptService.decrypt(encData);
                if (decrypt == null)
                    return ResponseEntity.badRequest().build();
                else {
                    String[] decResult = decrypt.split("\\|");
                    UUID uuid = UUID.fromString(decResult[0]);
                    long time = Long.parseLong(decResult[1]);
                    if (System.currentTimeMillis() - time <= requestTimeout) {
                        //성공
                        log.info("uuid : {} requested mdm", uuid);
                        if (service.isUserExist(uuid)) {
                            service.executeMDM(uuid, service.getMDMStatus(uuid));
                            return ResponseEntity.ok().build();
                            //서버랑 연결 안되있으면 nfc 태깅시 에러
                        }
                        else
                            return ResponseEntity.internalServerError().build();
                    }
                    else
                        return ResponseEntity.badRequest().build();
                }
            }
            else {
                //card 구현부
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
