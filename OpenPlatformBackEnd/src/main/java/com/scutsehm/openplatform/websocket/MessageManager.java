package com.scutsehm.openplatform.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageManager {
    @Autowired
    private MessageSocket messageSocket;

    public boolean sendNormalMessage(String message, String userId){
        return messageSocket.sendMessage(message, userId);
    }
}
