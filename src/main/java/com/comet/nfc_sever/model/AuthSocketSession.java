package com.comet.nfc_sever.model;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Data
public class AuthSocketSession {

    private UUID uuid; //key
    private WebSocketSession session; //웹소켓 세션
    private boolean pong; //pong 했는지
    private long lastPong; //millisecond

}
