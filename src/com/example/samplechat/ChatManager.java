package com.example.samplechat;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import com.quickblox.chat.model.QBChatMessage;

public interface ChatManager {


    void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException;

    void release() throws XMPPException;
	
}
