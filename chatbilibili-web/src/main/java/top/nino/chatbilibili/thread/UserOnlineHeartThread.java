package top.nino.chatbilibili.thread;


import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.http.HttpHeartBeatData;

public class UserOnlineHeartThread extends Thread{
//	private static Logger LOGGER = LogManager.getLogger(UserOnlineHeartThread.class);
	public volatile boolean FLAG = false;
	@Override
	public void run() {
		// TODO 自动生成的方法存根
		super.run();
		while (!FLAG) {
			if (FLAG) {
				return;
			}
			if(GlobalSettingCache.USER==null) {
				return;
			}
			try {
				Thread.sleep(300*1000);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
//				LOGGER.info("在线心跳线程post 5m关闭:"+e);
//				e.printStackTrace();
			}
			HttpHeartBeatData.httpPostUserOnlineHeartBeat();
		}
	}
}
