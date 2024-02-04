package top.nino.chatbilibili.rest;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import top.nino.chatbilibili.http.HttpUserData;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


@Controller
@ServerEndpoint("/danmu/sub")
public class DanmuWebsocket {
	private Logger LOGGER = LogManager.getLogger(DanmuWebsocket.class);
	private static CopyOnWriteArraySet<DanmuWebsocket> webSocketServers = new CopyOnWriteArraySet<>();
	private Session session;
	
	@OnOpen
	public void onOpen(Session session) {
		this.session=session;
		webSocketServers.add(this);
	}
	
	@OnClose
	public void onClose() {
		webSocketServers.remove(this);
	}
	
	@OnMessage
	public void onMessage(String message) throws IOException {
		//反向发送 23333333333 (滑稽
		// 主动向房间发送消息需要调用http请求. (https://api.live.bilibili.com/msg/send)
		HttpUserData.httpPostSendBarrage(message);
//		for(DanmuWebsocket danmuWebsocket:webSocketServers) {
//			danmuWebsocket.session.getBasicRemote().sendText(message);
//		}
	}
	
	
	@OnError
	public void onError(Session session,Throwable error) {
		LOGGER.error(error);
	}

	public void sendMessage(String message) throws IOException {
		// 主动调用房间连接后才可接受房间内消息
		for(DanmuWebsocket danmuWebsocket:webSocketServers) {
			synchronized (danmuWebsocket.session) {
				danmuWebsocket.session.getBasicRemote().sendText(message);
			}
			
		}
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	
}
