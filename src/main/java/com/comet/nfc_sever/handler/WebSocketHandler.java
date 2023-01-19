package com.comet.nfc_sever.handler;

import com.comet.nfc_sever.dto.SocketMessageDto;
import com.comet.nfc_sever.model.AuthSocketSession;
import com.comet.nfc_sever.service.NfcUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    //멀티쓰레드 concurrent 방지 위한 동기화 set
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>()); //이건 세션 필요없을지도?
    private final Set<AuthSocketSession> socketSessions = Collections.synchronizedSet(new HashSet<>());

    // TODO 세션 인증 시간 두기, 구분짓기
    private final ObjectMapper mapper;

    private final NfcUserService service;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("connection established : " + session.getId());
        sessions.add(session);
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
        log.info("mapped value : " + dto.getStatus() );
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message)  {
        log.info("receive pong message from session " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("connection closed : " + session.getId());
        sessions.remove(session);
    }

    //async socket 검증
    protected void executeAuth(WebSocketSession session) {
        /*
         logic = async 로 몇초(짧음)간 기다린 다음, 세션이 auth 리스트에 담겨있는지 확인
        */
    }
}
