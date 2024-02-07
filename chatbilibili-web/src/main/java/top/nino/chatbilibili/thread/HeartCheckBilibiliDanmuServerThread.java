package top.nino.chatbilibili.thread;


import lombok.extern.slf4j.Slf4j;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.core.HexUtils;

/**
 * 心跳检测线程
 * @author nino
 */
@Slf4j
public class HeartCheckBilibiliDanmuServerThread extends Thread {

	public volatile boolean HFLAG = false;

	/**
	 * 如果仍在和B弹幕服务器保持连接
	 * 就每隔3秒钟，发送心跳包，使得可以继续保持连接
	 */
	@Override
	public void run() {
		while (!HFLAG) {
			if(GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
				try {
					Thread.sleep(30000);
					GlobalSettingConf.bilibiliWebSocketProxy.send(HexUtils.fromHexString(GlobalSettingConf.HEART_BYTE));
				} catch (Exception e) {
					log.info("心跳线程关闭", e);
				}
			}

		}
	}

}
