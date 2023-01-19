package com.comet.nfc_sever.dto;

import lombok.Data;

import java.util.List;

@Data
public class SocketMessageDto {

    public enum Status {
        HAND_SHAKE, PONG, EXECUTE_MDM
    }

    private Status status;
    private Object data;

}

