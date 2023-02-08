package com.comet.nfc_sever.service;

import com.comet.nfc_sever.dto.MDMStatusDto;
import com.comet.nfc_sever.dto.NfcUserRequestDto;
import com.comet.nfc_sever.handler.WebSocketHandler;
import com.comet.nfc_sever.model.NfcUser;
import com.comet.nfc_sever.repository.NfcUserRepository;
import com.comet.nfc_sever.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class NfcUserService {

    private NfcUserRepository repository;
    private WebSocketHandler handler;

    @Transactional
    public boolean isUserExist(UUID input) {
        return repository.existsByUuid(input);
    }

    @Transactional
    public void createUser(NfcUserRequestDto dto) {
        log.info("{} : {}", dto.getId(), StringUtil.isUUID(dto.getId()));
        //dto 검증은 완료됨.
        log.info("User created. id : " + repository.save(dto.toEntity()).getId());
    }

    public String getAuthKey(UUID uuid) {
        return repository.findByUuid(uuid).orElseThrow().getAuthKey();
    }

    //여기는 실질적 response 응답시.
    @Transactional
    public void setUserLockStatus(UUID uuid, boolean value) {
        //실질적으로 잠겼는지, 풀렸는지 여부 체크하는곳.
        repository.findByUuid(uuid).ifPresent((user) -> user.setLocked(value));
    }
    @Transactional
    public void executeMDM(UUID uuid, boolean value) {
        //mdm 요청만 보내는곳.
        Optional<NfcUser> user = repository.findByUuid(uuid);
        if (user.isPresent()) {
            NfcUser nfcUser = user.get();
            handler.sendMDMCommand(nfcUser.getUuid(), value);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent") //is method check
    @Transactional
    public boolean getMDMStatus(UUID uuid) {
        return isUserExist(uuid) && repository.findByUuid(uuid).get().isLocked();
    }

    @Transactional
    public List<MDMStatusDto> getAllUserStatus() {
        List<MDMStatusDto> result = new ArrayList<>();
        List<NfcUser> users = repository.findAll();
        for (NfcUser user : users)
            //get server connected
            result.add(new MDMStatusDto(user.getId(), user.getUuid(), user.getAuthKey(), user.getDeleteKey(), user.isLocked(), handler.existByUUID(user.getUuid())));
        return result;

    }

    @Transactional
    public boolean disconnectUser(UUID uuid) {
        if (handler.existByUUID(uuid)) {
            return handler.disconnect(uuid);
        }
        return false;
    }
}
