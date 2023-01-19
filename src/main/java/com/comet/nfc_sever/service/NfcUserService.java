package com.comet.nfc_sever.service;

import com.comet.nfc_sever.repository.NfcUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NfcUserService {

    private NfcUserRepository repository;

}
