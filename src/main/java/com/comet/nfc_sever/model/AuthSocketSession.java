package com.comet.nfc_sever.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class AuthSocketSession {

    private final UUID uuid; //key
    private final WebSocketSession session; //웹소켓 세션
    private boolean isPong = true; // 응답여부, 1회는 응답됨.
    private Twin<Boolean, Boolean> isMdmRequested = new Twin<>(false, false);


}
