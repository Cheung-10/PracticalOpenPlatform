package com.scutsehm.openplatform.websocket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/ws/message/{userId}")
@Slf4j
public class MessageSocket {

    /**
     * 在线socket session
     */
    private static Map<String, Session> clients = new ConcurrentHashMap<String, Session>();

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        //注册session
        clients.put(userId, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.info("服务端发生了错误" + error.getMessage());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session) {

    }


    @OnMessage
    public void onMessage(String message, Session session) {
        log.info(message);
    }


    public boolean sendMessage(String message, String userId)  {
        try {
            Session session = clients.get(userId);
            if(session==null){
                return false;
            }
            session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
