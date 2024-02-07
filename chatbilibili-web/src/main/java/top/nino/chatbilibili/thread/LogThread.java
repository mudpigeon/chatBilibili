package top.nino.chatbilibili.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.core.LogFileUtils;


/**
 * @author nino
 */
@Slf4j
public class LogThread extends Thread{

	public volatile boolean FLAG = false;

	@Override
	public void run() {

		while (!FLAG) {

			if(ObjectUtils.isEmpty(GlobalSettingConf.bilibiliWebSocketProxy)&& !GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
				return;
			}

			if(CollectionUtils.isEmpty(GlobalSettingConf.logList) || StringUtils.isBlank(GlobalSettingConf.logList.get(0))) {
				synchronized (GlobalSettingConf.logThread) {
					try {
						GlobalSettingConf.logThread.wait();
					} catch (InterruptedException e) {
//						LOGGER.info("日志线程关闭:" + e);
					}
				}
			}

			String logString = GlobalSettingConf.logList.get(0);
			LogFileUtils.logFile(logString, GlobalSettingConf.GLOBAL_SETTING_FILE_NAME, GlobalSettingConf.ROOM_ID);

			GlobalSettingConf.logList.remove(0);

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
