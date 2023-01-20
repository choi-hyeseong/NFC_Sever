package com.comet.nfc_sever.controller;

import com.comet.nfc_sever.dto.NfcUserRequestDto;
import com.comet.nfc_sever.model.EncryptTwin;
import com.comet.nfc_sever.response.WebResponse;
import com.comet.nfc_sever.service.EncryptService;
import com.comet.nfc_sever.service.NfcUserService;
import com.comet.nfc_sever.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private NfcUserService userService;
    private EncryptService encryptService;

    @PostMapping("/auth")
    public ResponseEntity<WebResponse<EncryptTwin>> auth(@RequestBody NfcUserRequestDto dto) {
        // TODO @valid 적용해서 검증코드 분리하기.
        log.info(dto.getId());
        String id = dto.getId();
        if (StringUtil.isUUID(id))
            return new ResponseEntity<>(new WebResponse<>("UUID must be encrypted", null), HttpStatus.BAD_REQUEST);

        String uuid = encryptService.decrypt(id);
        if (uuid == null || !StringUtil.isUUID(uuid))
            return new ResponseEntity<>(new WebResponse<>("Not Valid Encryption.", null), HttpStatus.BAD_REQUEST);

        UUID input = UUID.fromString(uuid);
        if (userService.isUserExist(input))
            return new ResponseEntity<>(new WebResponse<>("Already Exist Id.", null), HttpStatus.BAD_REQUEST);

        String deleteId = StringUtil.generateRandomString(10); //10자리 삭제 코드
        String authId = StringUtil.generateRandomString(10); //10자리 인증 코드
        String encDelete = encryptService.AESEncrypt(deleteId, input.toString());
        String encAuth = encryptService.AESEncrypt(authId, input.toString());

        if (encDelete != null && encAuth != null) {
            dto.setId(input.toString());
            dto.setDel(deleteId);
            dto.setAuth(authId);
            userService.createUser(dto);
            return new ResponseEntity<>(new WebResponse<>("Created!", new EncryptTwin(encAuth, encDelete)), HttpStatus.OK);
        }
        else
            return ResponseEntity.internalServerError().build();
    }

}
