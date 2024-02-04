package top.nino.chatbilibili.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.nino.api.model.room.Room;


import java.net.URISyntaxException;


/**
 * @author nino
 */
@Slf4j
public class WebSocketProxy extends Websocket {


	public WebSocketProxy(String url, Room room) throws URISyntaxException, InterruptedException {
		super(url, room);
		log.info("Connectin(连接中)...........................................");
		super.connectBlocking();
		log.info("Connecting Success(连接成功)");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		super.onClose(code, reason, remote);
	
	}
}
