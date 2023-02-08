package com.comet.nfc_sever.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MDMStatusDto {

    private long id;
    private UUID uuid;
    private String auth;
    private String delete;
    private boolean isMDMEnabled;
    private boolean isServerConnected;

}
