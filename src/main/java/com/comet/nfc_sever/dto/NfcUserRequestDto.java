package com.comet.nfc_sever.dto;

import com.comet.nfc_sever.model.NfcUser;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class NfcUserRequestDto {

    @NotNull
    private String id;
    //밑에 두개는 서버단에서 추가.
    @Nullable
    private String del;
    @Nullable
    private String auth;

    public NfcUser toEntity() {
        return NfcUser.builder().uuid(UUID.fromString(id)).authKey(auth).deleteKey(del).build();
    }
}
