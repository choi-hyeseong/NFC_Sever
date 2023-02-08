package com.comet.nfc_sever.controller;

import com.comet.nfc_sever.dto.MDMStatusDto;
import com.comet.nfc_sever.dto.MdmRequestDto;
import com.comet.nfc_sever.response.WebResponse;
import com.comet.nfc_sever.service.EncryptService;
import com.comet.nfc_sever.service.NfcUserService;
import com.comet.nfc_sever.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/status")
    public ResponseEntity<WebResponse<List<MDMStatusDto>>> status() {
        // local 에서만 접근 가능하게 설정.
        String msg = "OK";
        List<MDMStatusDto> list = service.getAllUserStatus();
        if (list.isEmpty())
            msg = "Data is empty";
        return new ResponseEntity<>(new WebResponse<>(msg, list), HttpStatus.OK);
    }

    @PostMapping("/server/request") //서버에서 요청하는 리퀘스트, dto의 데이터는 uuid
    public ResponseEntity<HttpStatus> serverRequest(@RequestBody MdmRequestDto dto) {
        HttpStatus status;
        if (!StringUtil.isUUID(dto.getData()))
            status = HttpStatus.BAD_REQUEST;
        else {
            UUID data = UUID.fromString(dto.getData());
            if (service.isUserExist(data)) {
                service.executeMDM(data, !service.getMDMStatus(data));
                status = HttpStatus.OK;
            }
            else
                status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(status, status);
    }

    @PostMapping("/server/disconnect") //서버에서 요청하는 리퀘스트, dto의 데이터는 uuid
    public ResponseEntity<Boolean> serverDisconnect(@RequestBody MdmRequestDto dto) {
        boolean result;
        if (!StringUtil.isUUID(dto.getData()))
            result = false;
        else
            result = service.disconnectUser(UUID.fromString(dto.getData()));
        return new ResponseEntity<>(result, result ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
