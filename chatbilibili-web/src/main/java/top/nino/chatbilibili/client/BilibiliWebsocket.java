package top.nino.chatbilibili.client;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ObjectUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import top.nino.api.model.room.RoomAnchorInfo;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.thread.ReConnThread;
import top.nino.chatbilibili.client.utils.ParseWebsocketMessageUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

/**
 * @author nino
 */
@Slf4j
public class BilibiliWebsocket extends WebSocketClient {


	public BilibiliWebsocket(String url, RoomAnchorInfo roomAnchorInfo) throws URISyntaxException {
		super(new URI(url));
		log.info("已尝试连接至服务器地址:{}", url);
		log.info("真实房间号为:{}", roomAnchorInfo.getRoomid());
		log.info("主播名字为:{}", roomAnchorInfo.getUname());
	}

	@Override
	public void onOpen(ServerHandshake handshakeData) {
		log.info("和b站直播间的websocket连接窗口已打开)");
	}

	@Override
	public void onMessage(ByteBuffer message) {
		if(ObjectUtils.isNotEmpty(GlobalSettingCache.parseDanmuMessageThread) && ! GlobalSettingCache.parseDanmuMessageThread.closeFlag) {
			try {
				ParseWebsocketMessageUtils.parseMessage(message);
			} catch (Exception e) {
				log.info("解析websocket包错误", e);
			}
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		log.info("websocket connect close(连接已经断开)，纠错码:" + code);
		GlobalSettingCache.heartCheckBilibiliDanmuServerThread.HFLAG = true;
		GlobalSettingCache.parseDanmuMessageThread.closeFlag = true;
		if (code != 1000) {
			log.info("websocket connect close(连接意外断开，正在尝试重连)，错误码:" + code);
			if (!GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
				if (GlobalSettingCache.reConnThread != null) {
					if (GlobalSettingCache.reConnThread.getState().toString().equals("TERMINATED")) {
						GlobalSettingCache.reConnThread = new ReConnThread();
						GlobalSettingCache.reConnThread.start();
					} else {

					}
				} else {
					GlobalSettingCache.reConnThread = new ReConnThread();
					GlobalSettingCache.reConnThread.start();
				}
			} else {
				GlobalSettingCache.reConnThread.RFLAG = true;
			}
		}
	}

	@Override
	public void onError(Exception ex) {
		log.error("[错误信息，请将log文件下的日志发送给管理员]websocket connect error,message:" + ex.getMessage());
		log.info("尝试重新链接");
		synchronized (GlobalSettingCache.bilibiliWebSocketProxy) {
			GlobalSettingCache.bilibiliWebSocketProxy.close(1006);
			if (!GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
				if (GlobalSettingCache.reConnThread != null) {
					if (GlobalSettingCache.reConnThread.getState().toString().equals("TERMINATED")) {
						GlobalSettingCache.reConnThread = new ReConnThread();
						GlobalSettingCache.reConnThread.start();
					} else {

					}
				} else {
					GlobalSettingCache.reConnThread = new ReConnThread();
					GlobalSettingCache.reConnThread.start();
				}
			} else {
				GlobalSettingCache.reConnThread.RFLAG = true;
			}
		}
	}

	public void onMessage(String message) {

	}

}
