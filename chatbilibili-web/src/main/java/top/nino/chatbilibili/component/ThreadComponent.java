package top.nino.chatbilibili.component;



import top.nino.chatbilibili.conf.base.AllSettingConfig;
import top.nino.chatbilibili.conf.set.AdvertSetConf;
import top.nino.chatbilibili.conf.set.AutoReplySetConf;


/**
 * @author nino
 */
public interface ThreadComponent {

	void closeAll();

	void closeUser(boolean close);

	// 开启处理弹幕包线程
	void startParseMessageThread();

	// 开启心跳线程
	void startHeartCheckBilibiliDanmuServerThread();

	// 开启日志线程
	void startLogThread();


	// 开启公告线程 need login
	void startAdvertThread(AdvertSetConf advertSetConf);

	// 开启自动回复线程 need login
//	boolean startAutoReplyThread(CenterSetConf centerSetConf);
	void startAutoReplyThread(AutoReplySetConf autoReplySetConf);
	// 开启发送弹幕线程 need login
	void startSendBarrageThread();

	// 开启用户在线线程 need login
	boolean startUserOnlineThread();
	
	// 开启用户小心心线程
	boolean startSmallHeartThread();


	boolean startFollowShieldThread(int time);

	// 设置处理弹幕包线程
	void setParseMessageThread(AllSettingConfig allSettingConfig);


	// 设置自动回复线程 need login
	void setAutoReplyThread(AutoReplySetConf autoReplySetConf);

	// 关闭处理弹幕包线程 core
	void closeParseMessageThread();

	// 关闭心跳线程 core
	void closeHeartByteThread();
	
	// 关闭用户心跳线程
	void closeSmallHeartThread();

	// 关闭日志线程
	void closeLogThread();

	void closeAdvertThread();

	void closeAutoReplyThread();

	void closeSendBarrageThread();

	void closeGiftShieldThread();

	void closeFollowShieldThread();

	void closeWelcomeShieldThread();

	// 关闭用户在线线程
	void closeUserOnlineThread();

}
