package cn.dlb.bim.websocket;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import cn.dlb.bim.action.JsonGeometryQueryAction;
import cn.dlb.bim.action.StreamingGeometryQueryAction;
import cn.dlb.bim.component.PlatformServer;
import cn.dlb.bim.dao.entity.ConcreteRevision;
import cn.dlb.bim.ifc.stream.query.QueryContext;
import cn.dlb.bim.service.BimService;

@Component("GeometrySocketHandler")
public class GeometrySocketHandler implements WebSocketHandler {
	private static final Logger logger = LoggerFactory.getLogger(GeometrySocketHandler.class);

	private static final List<WebSocketSession> users = new ArrayList<WebSocketSession>();
    @Autowired
    private PlatformServer server;
    @Autowired
    private BimService bimService;

	/**
	 * after connection establish
	 */
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		users.add(session);
		logger.info("connect success...");
		String rid = session.getAttributes().get("rid").toString();
		JsonGeometryQueryAction longAction = new JsonGeometryQueryAction(bimService, Integer.valueOf(rid), session);
		longAction.execute();
	}

	/**
	 * process the received message
	 */
	@Override
	public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage)
			throws Exception {
		return;
	}

	@Override
	public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
		users.remove(webSocketSession);
		// server.getLongActionManager().cancelLongActionByWebSocketSessionRelated(webSocketSession);
		if (webSocketSession.isOpen()) {

			webSocketSession.close();
		}
		logger.info("connenction error,close the connection...");
	}

	@Override
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
		users.remove(webSocketSession);
		// server.getLongActionManager().cancelLongActionByWebSocketSessionRelated(webSocketSession);
		logger.info("close the connenction..." + closeStatus.toString());
	}

	@Override
	public boolean supportsPartialMessages() {
		return true;
	}
}
