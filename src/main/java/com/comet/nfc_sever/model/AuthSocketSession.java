package com.comet.nfc_sever.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthSocketSession {

    private UUID uuid; //key
    private WebSocketSession session; //웹소켓 세션
    private boolean isPong; // 응답여부

}
