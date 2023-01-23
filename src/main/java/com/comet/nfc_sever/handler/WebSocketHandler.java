package com.comet.nfc_sever.handler;

import com.comet.nfc_sever.dto.SocketMessageDto;
import com.comet.nfc_sever.model.AuthSocketSession;
import com.comet.nfc_sever.model.Twin;
import com.comet.nfc_sever.service.NfcUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    //멀티쓰레드 concurrent 방지 위한 동기화 set
    private final Set<Twin<Long, WebSocketSession>> sessions = Collections.synchronizedSet(new HashSet<>()); //접속 시간, 세션 Twin
    private final Set<AuthSocketSession> authSessions = Collections.synchronizedSet(new HashSet<>()); //인증된 세션

    // TODO 세션 인증 시간 두기, 구분짓기
    private final ObjectMapper mapper;
    private final NfcUserService service;
    @Value("${nfc.server.auth-timeout}")
    private long timeout;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("connection established : " + session.getId());
        sessions.add(new Twin<>(System.currentTimeMillis(), session));
    }

    //나중에 error exception 처리하기
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        log.info("receive message from session " + session.getId() + " " + message.getPayload());
        String payload = message.getPayload();
        SocketMessageDto dto = mapper.readValue(payload, SocketMessageDto.class); //JSON.stringify() 사용
        if (dto.getStatus() == SocketMessageDto.Status.HAND_SHAKE) {
            //logic
        }
        else {

        }
        log.info("mapped value : " + dto.getStatus());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        log.info("receive pong message from session " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        //여기서는 리스트 세션 종료.
        removeSession(session);
        log.info("connection closed : " + session.getId());
    }

    //async socket 검증
    @Scheduled(fixedDelayString = "${nfc.server.auth-interval}")
    public void executeAuth() throws IOException {
        synchronized (sessions) {
            for (Twin<Long, WebSocketSession> session : new HashSet<>(sessions)) {
                //동시성 오류 방지위한 clone
                WebSocketSession socketSession = session.getSecond();
                long current = System.currentTimeMillis();
                long gap = current - session.getFirst();
                boolean isAuth = existBySession(socketSession);
                boolean isTimeout = gap >= timeout; //제시간내에 인증못하면 close
                if (isAuth || isTimeout) {
                    if (isTimeout && socketSession.isOpen())
                        //타임아웃 처리
                        socketSession.close();
                    sessions.remove(session);
                }
            }
        }
    }

    private boolean existBySession(WebSocketSession session) {
        return authSessions.stream().anyMatch((auth) -> auth.getSession().equals(session));
    }

    private void removeSession(WebSocketSession session) {
        // TODO 구현필수. 둘다 삭제하기
    }
}
