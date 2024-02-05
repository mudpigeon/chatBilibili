package top.nino.chatbilibili.thread;


import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.http.HttpHeartBeatData;

public class HeartBeatThread extends Thread{
//	private static Logger LOGGER = LogManager.getLogger(HeartBeatThread.class);
	public volatile boolean FLAG = false;
	@Override
	public void run() {
		// TODO 自动生成的方法存根
		super.run();
		while (!FLAG) {
			if (FLAG) {
				return;
			}
			if(GlobalSettingConf.USER==null) {
				return;
			}
			HttpHeartBeatData.httpGetHeartBeatOrS(null);
			try {
				Thread.sleep(96*1000);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
//				LOGGER.info("在线心跳线程96s关闭"+e);
//				e.printStackTrace();
			}
		}
	}
}
