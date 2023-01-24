package com.comet.nfc_sever.handler;

import com.comet.nfc_sever.dto.SocketMessageDto;
import com.comet.nfc_sever.model.AuthSocketSession;
import com.comet.nfc_sever.model.Twin;
import com.comet.nfc_sever.service.EncryptService;
import com.comet.nfc_sever.service.NfcUserService;
import com.comet.nfc_sever.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

import static com.comet.nfc_sever.dto.SocketMessageDto.Status;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    // TODO concurrent 문제 확인
    //멀티쓰레드 concurrent 방지 위한 동기화 set
    private final Set<Twin<Long, WebSocketSession>> sessions = Collections.synchronizedSet(new HashSet<>()); //접속 시간, 세션 Twin
    private final Set<AuthSocketSession> authSessions = Collections.synchronizedSet(new HashSet<>()); //인증된 세션

    private final ObjectMapper mapper;
    private final NfcUserService service;
    private final EncryptService encryptService;

    // handler 입장에서는 천천히 로딩해도 됨.
    public WebSocketHandler(ObjectMapper mapper, EncryptService encryptService, @Lazy NfcUserService service) {
        this.mapper = mapper;
        this.service = service;
        this.encryptService = encryptService;
    }

    @Value("${nfc.server.auth-timeout}")
    private long timeout;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("connection established : {}", session.getId());
        sessions.add(new Twin<>(System.currentTimeMillis(), session));
    }

    //나중에 error exception 처리하기
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("receive message from session {}, {}", session.getId(), message.getPayload());
        String payload = message.getPayload();
        SocketMessageDto dto = mapper.readValue(payload, SocketMessageDto.class); //JSON.stringify() 사용
        SocketMessageDto.Status status = dto.getStatus();
        log.info("mapped value : {}", dto.getStatus());
        if (existBySession(session)) {
            //인증 된경우
            AuthSocketSession socketSession = findBySession(session).get(); //exist 로 체크후 작동
            if (status == Status.PONG) {
                socketSession.setPong(true); //PONG
                //mdm logic
                if (socketSession.getIsMdmRequested().getFirst()) {
                    //request 여부
                    boolean value = socketSession.getIsMdmRequested().getSecond();
                    socketSession.setIsMdmRequested(new Twin<>(false, false));
                    sendMdmRequest(socketSession, value);
                }

            }

            else if (status == Status.RESPONSE) {
               //MDM RESPONSE
               //내일의 내가 구현해줄거
            }
            else
                sendMessage("Bad Request", session);
        }
        else {
            if (status == SocketMessageDto.Status.HAND_SHAKE) {
                //핸드 쉐이킹만 인식.
                String data = dto.getData(); //UUID 암호화된 데이터
                String decrypt = encryptService.decrypt(data);
                if (decrypt == null) { //복호화 실패
                    sendMessage("UUID decrypt failed", session);
                    return;
                }

                UUID uuid = UUID.fromString(decrypt);
                if (!service.isUserExist(uuid)) {
                    sendMessage("That UUID isn't exist", session);
                    return;
                }

                if (existByUUID(uuid)) {
                    //이미 인증된 세션이 존재하는경우
                    sendMessage("Already Authentication Session exists.", session);
                    return;
                }

                authSessions.add(new AuthSocketSession(uuid, session));
                sendMessage("Authentication success", session);

            }
            else
                sendMessage("Bad Request", session);

        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        //여기서는 리스트 세션 종료.
        removeSession(session);
        log.info("connection closed : {}", session.getId());
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

    @Scheduled(fixedDelayString = "${nfc.server.ping-interval}")
    public void ping() {
        synchronized (authSessions) {
            //리스트 삭제 위한 시도
            new HashSet<>(authSessions).forEach((auth) -> {
                try {
                    WebSocketSession socketSession = auth.getSession();
                    boolean isPong = auth.isPong();
                    if (!isPong || !socketSession.isOpen()) {
                        //응답 못함 or 닫힘
                        if (socketSession.isOpen())
                            socketSession.close();
                        authSessions.remove(auth);
                    }
                    else {
                        auth.setPong(false); //다시 pong 대기
                        sendObject(new SocketMessageDto(Status.PING, ""), socketSession);
                    }
                }
                catch (Exception e) {
                    log.error(e.getLocalizedMessage());
                }
            });

        }
    }

    public void sendMDMCommand(UUID uuid, boolean value) {
        findByUUID(uuid).ifPresent(authSocketSession -> authSocketSession.setIsMdmRequested(new Twin<>(true, value))); //다음 핑퐁시 작동됨.
    }

    private boolean existBySession(WebSocketSession session) {
        return authSessions.stream().anyMatch((auth) -> auth.getSession().equals(session));
    }

    private boolean existByUUID(UUID uuid) {
        return authSessions.stream().anyMatch((auth) -> auth.getUuid().equals(uuid));
    }

    private void sendMdmRequest(AuthSocketSession session, boolean value) throws IOException {
        // 실질적인 mdm 리퀘스트 부분..
        // AUTH|TRASH_STR|value
        WebSocketSession socketSession = session.getSession();
        String auth = service.getAuthKey(session.getUuid());
        String encAuth = encryptService.AESEncrypt(auth + "|" + StringUtil.generateRandomString(5) + "|" + value, session.getUuid().toString());
        SocketMessageDto dto = new SocketMessageDto(Status.EXECUTE_MDM, encAuth);
        sendObject(dto, socketSession);
    }

    private Optional<AuthSocketSession> findByUUID(UUID uuid) {
        return authSessions.stream().filter((auth) -> auth.getUuid().equals(uuid)).findFirst();
    }

    private Optional<AuthSocketSession> findBySession(WebSocketSession session) {
        return authSessions.stream().filter((auth) -> auth.getSession().equals(session)).findFirst();
    }

    private void removeSession(WebSocketSession session) {
        sessions.removeIf((sess) -> sess.getSecond().equals(session));
        authSessions.removeIf((auth) -> auth.getSession().equals(session));
    }

    private void sendMessage(String text, WebSocketSession session) throws IOException {
        sendObject(new SocketMessageDto(SocketMessageDto.Status.RESPONSE, text), session);
    }

    private void sendObject(SocketMessageDto dto, WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage(mapper.writeValueAsString(dto)));
    }
}
