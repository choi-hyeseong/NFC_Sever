package com.comet.nfc_sever.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class EncryptTwin {

    private String auth;
    private String delete;

    //변수 이름 매핑용
    public EncryptTwin(Twin<String, String> input) {
        auth = input.getFirst();
        delete = input.getSecond();
    }

}
