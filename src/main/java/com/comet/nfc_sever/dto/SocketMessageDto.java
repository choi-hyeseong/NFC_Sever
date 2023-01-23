package com.comet.nfc_sever.dto;

import lombok.Data;

@Data
public class SocketMessageDto {

    public enum Status {
        HAND_SHAKE, PING, PONG, EXECUTE_MDM
    }

    private Status status;
    private Object data;

}

