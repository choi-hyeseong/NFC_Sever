package com.comet.nfc_sever.controller;

import com.comet.nfc_sever.dto.NfcUserRequestDto;
import com.comet.nfc_sever.model.EncryptTwin;
import com.comet.nfc_sever.response.WebResponse;
import com.comet.nfc_sever.service.EncryptService;
import com.comet.nfc_sever.service.NfcUserService;
import com.comet.nfc_sever.util.StringUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private NfcUserService userService;
    private EncryptService encryptService;

    @PostMapping("/auth")
    public ResponseEntity<WebResponse<EncryptTwin>> auth(@Valid @RequestBody NfcUserRequestDto dto) {
        String id = dto.getId();
        String deleteId = StringUtil.generateRandomString(10); //10자리 삭제 코드
        String authId = StringUtil.generateRandomString(10); //10자리 인증 코드
        String encDelete = encryptService.AESEncrypt(deleteId, id);
        String encAuth = encryptService.AESEncrypt(authId, id);

        if (encDelete != null && encAuth != null) {
            dto.setId(id);
            dto.setDel(deleteId);
            dto.setAuth(authId);
            userService.createUser(dto);
            return new ResponseEntity<>(new WebResponse<>("Created!", new EncryptTwin(encAuth, encDelete)), HttpStatus.OK);
        }
        else
            return ResponseEntity.internalServerError().build();
    }

}
