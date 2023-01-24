package com.comet.nfc_sever.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SocketMessageDto {

    public enum Status {
        HAND_SHAKE, PING, PONG, EXECUTE_MDM, RESPONSE
    }

    private Status status;
    private String data; //암호화된 데이터 전송

    /*
      HAND_SHAKE = DATA(UUID) - server
      PING, PONG = NONE - client-server
      EXECUTE_MDM = ENCRYPT DATA(STR) - client
     */

}

