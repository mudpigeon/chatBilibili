package top.nino.chatbilibili.client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;


import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @author nino
 */
@Slf4j
@Controller
@ServerEndpoint("/chatbilibili/danmu/sub")
public class ChatBilibiliWebsocketController {

	private static final CopyOnWriteArraySet<ChatBilibiliWebsocketController> webSocketServerList = new CopyOnWriteArraySet<>();

	private Session session;

	public void sendMessageToView(String message) throws IOException {
		// 主动调用房间连接后才可接受房间内消息
		for(ChatBilibiliWebsocketController chatBilibiliWebsocketController : webSocketServerList) {
			synchronized (chatBilibiliWebsocketController.session) {
				chatBilibiliWebsocketController.session.getBasicRemote().sendText(message);
			}

		}
	}

	@OnOpen
	public void onOpen(Session session) {
		log.info("本地websocket服务器已收到前端连接。");
		this.session = session;
		webSocketServerList.add(this);
	}

	/**
	 * 收到前端发送的消息
	 * @param message
	 * @throws IOException
	 */
	@OnMessage
	public void onMessage(String message) throws IOException {
		//反向发送 23333333333 (滑稽
		// 主动向房间发送消息需要调用http请求. (https://api.live.bilibili.com/msg/send)
//		HttpUserData.httpPostSendBarrage(message);
//		for(DanmuWebsocket danmuWebsocket:webSocketServers) {
//			danmuWebsocket.session.getBasicRemote().sendText(message);
//		}
	}


	@OnClose
	public void onClose() {
		webSocketServerList.remove(this);
	}
	
	@OnError
	public void onError(Session session,Throwable error) {
		log.error("本地websocket异常", error);
	}
	
	
}
