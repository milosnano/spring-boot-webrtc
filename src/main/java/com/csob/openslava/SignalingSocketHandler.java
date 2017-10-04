package com.csob.openslava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;


public class SignalingSocketHandler extends TextWebSocketHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SignalingSocketHandler.class);

	private static final String TYPE_INIT = "init";
	private static final String TYPE_LOGOUT = "logout";

	/**
	 * Cache of sessions by users.
	 */
	private Map<String, WebSocketSession> connectedUsers = new HashMap<>();


	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		LOG.info("[" + session.getId() + "] Connection established " + session.getId());

		// send the message to all other peers, that new men its being registered
		final SignalMessage newMenOnBoard = new SignalMessage();
		newMenOnBoard.setType(TYPE_INIT);
		newMenOnBoard.setSender(session.getId());

		connectedUsers.values().forEach(webSocketSession -> {
			try {
				webSocketSession.sendMessage(new TextMessage(Utils.getString(newMenOnBoard)));
			} catch (Exception e) {
				LOG.warn("Error while message sending.", e);
			}
		});

		// put the session to the "cache".
		connectedUsers.put(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		LOG.info("[" + session.getId() + "] Connection closed " + session.getId() + " with status: " + status.getReason());
		removeUserAndSendLogout(session.getId());
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		LOG.info("[" + session.getId() + "] Connection error " + session.getId() + " with status: " + exception.getLocalizedMessage());
		removeUserAndSendLogout(session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		LOG.info("handleTextMessage : {}", message.getPayload());
	}


	private void removeUserAndSendLogout(final String sessionId) {

		connectedUsers.remove(sessionId);

		// send the message to all other peers, that new men its being registered
		final SignalMessage menOut = new SignalMessage();
		menOut.setType(TYPE_LOGOUT);
		menOut.setSender(sessionId);

		connectedUsers.values().forEach(webSocket -> {
			try {
				webSocket.sendMessage(new TextMessage(Utils.getString(menOut)));
			} catch (Exception e) {
				LOG.warn("Error while message sending.", e);
			}
		});
	}
}
