package com.comet.nfc_sever.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EncryptTwin {

    private String auth;
    private String delete;
}
