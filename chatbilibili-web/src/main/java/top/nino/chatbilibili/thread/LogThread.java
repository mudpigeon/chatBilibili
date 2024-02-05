package top.nino.chatbilibili.thread;

import org.apache.commons.lang3.StringUtils;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.tool.LogFileTools;


/**
 * @author nino
 */
public class LogThread extends Thread{
//	@SuppressWarnings("unused")
//	private Logger LOGGER = LogManager.getLogger(LogThread.class);
	public volatile boolean FLAG = false;
	@Override
	public void run() {
		// TODO 自动生成的方法存根
		String logString = null;
		super.run();
		while (!FLAG) {
			if (FLAG) {
				return;
			}
			if(GlobalSettingConf.webSocketProxy!=null&&!GlobalSettingConf.webSocketProxy.isOpen()) {
				return;
			}
			if(null!= GlobalSettingConf.logString&&!GlobalSettingConf.logString.isEmpty()&&StringUtils.isNotBlank(GlobalSettingConf.logString.get(0))) {
				logString = GlobalSettingConf.logString.get(0);
				LogFileTools.getlogFileTools().logFile(logString);
				GlobalSettingConf.logString.remove(0);
			}else {
				synchronized (GlobalSettingConf.logThread) {
					try {
						GlobalSettingConf.logThread.wait();
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
//						LOGGER.info("日志线程关闭:" + e);
					}
				}
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
	}
}
