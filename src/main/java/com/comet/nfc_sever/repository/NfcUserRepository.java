package com.comet.nfc_sever.repository;

import com.comet.nfc_sever.model.NfcUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NfcUserRepository extends JpaRepository<NfcUser, Long> {

    boolean existsByUuid(UUID input);

}
