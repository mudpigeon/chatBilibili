package top.nino.chatbilibili.thread;

import org.apache.commons.lang3.StringUtils;
import top.nino.chatbilibili.PublicDataConf;
import top.nino.core.LogFileTools;


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
			if(PublicDataConf.webSocketProxy!=null&&!PublicDataConf.webSocketProxy.isOpen()) {
				return;
			}
			if(null!=PublicDataConf.logString&&!PublicDataConf.logString.isEmpty()&&StringUtils.isNotBlank(PublicDataConf.logString.get(0))) {
				logString = PublicDataConf.logString.get(0);
				LogFileTools.getlogFileTools().logFile(logString);
				PublicDataConf.logString.remove(0);
			}else {
				synchronized (PublicDataConf.logThread) {
					try {
						PublicDataConf.logThread.wait();
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
