package com.comet.nfc_sever.controller;

import com.comet.nfc_sever.dto.MdmRequestDto;
import com.comet.nfc_sever.response.WebResponse;
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
    public ResponseEntity<WebResponse<String>> mdm(@RequestBody MdmRequestDto dto) {
        String data = dto.getData();
        ResponseEntity<WebResponse<String>> response;
        if ((data.contains("Android") || data.contains("Card")) && data.split("\\|").length == 2) {
            String[] split = data.split("\\|");
            if (data.contains("Android")) {
                String encData = split[0];
                String decrypt = encryptService.decrypt(encData);
                if (decrypt == null)
                    response = new ResponseEntity<>(new WebResponse<>("Decrypt Failed", "Retry Encryption."), HttpStatus.BAD_REQUEST);
                else {
                    String[] decResult = decrypt.split("\\|");
                    UUID uuid = UUID.fromString(decResult[0]);
                    long time = Long.parseLong(decResult[1]);
                    if (System.currentTimeMillis() - time <= requestTimeout) {
                        //성공
                        log.info("uuid : {} requested mdm", uuid);
                        if (service.isUserExist(uuid)) {
                            service.executeMDM(uuid, !service.getMDMStatus(uuid)); //반대로 보내야됨. get mdmdStatus는 response 들어와야 값이 변경되므로 여러번 태깅해도 반대의 상태로밖에 변하지 않는다. 잘짠듯 ㅋㅋㅋㅋ
                            response = new ResponseEntity<>(new WebResponse<>("OK", "Request success."), HttpStatus.OK);
                            //서버랑 연결 안되있으면 nfc 태깅시 에러
                        }
                        else
                            response = new ResponseEntity<>(new WebResponse<>("User Not Found.", "Internal Error Encountered."), HttpStatus.BAD_REQUEST); //미구현
                    }
                    else
                        response = new ResponseEntity<>(new WebResponse<>("Timeout.", "Request timeout"), HttpStatus.BAD_REQUEST); //미구현
                }
            }
            else {
                //card 구현부
                response = new ResponseEntity<>(new WebResponse<>("Not Implementation.", "Internal Error Encountered."), HttpStatus.BAD_REQUEST); //미구현
            }
        }
        else
            response = new ResponseEntity<>(new WebResponse<>("Internal Error", "Internal Error Encountered."), HttpStatus.INTERNAL_SERVER_ERROR); //카드 인식 올바르게 안된경우
        return response;
    }
}
