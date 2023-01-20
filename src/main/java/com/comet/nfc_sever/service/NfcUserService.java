package com.comet.nfc_sever.service;

import com.comet.nfc_sever.dto.NfcUserRequestDto;
import com.comet.nfc_sever.repository.NfcUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class NfcUserService {

    private NfcUserRepository repository;

    @Transactional
    public boolean isUserExist(UUID input) {
        return repository.existsByUuid(input);
    }

    @Transactional
    public void createUser(NfcUserRequestDto dto) {
        //dto 검증은 완료됨.
        log.info("User created. id : " + repository.save(dto.toEntity()).getId());
    }
}
