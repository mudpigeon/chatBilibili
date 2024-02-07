package top.nino.chatbilibili.service;



import top.nino.chatbilibili.AllSettingConfig;


/**
 * @author nino
 */
public interface ThreadService {

	void closeAll();

	void closeUser();

	// 开启处理弹幕包线程
	void startParseMessageThread();

	// 开启心跳线程
	void startHeartCheckBilibiliDanmuServerThread();

	// 开启日志线程
	void startLogThread();

	// 开启用户在线线程 need login
	boolean startUserOnlineThread();
	
	// 开启用户小心心线程
	boolean startSmallHeartThread();

	boolean startFollowShieldThread(int time);

	// 设置处理弹幕包线程
	void setParseMessageThread(AllSettingConfig allSettingConfig);



	// 关闭处理弹幕包线程 core
	void closeParseMessageThread();

	// 关闭心跳线程 core
	void closeHeartByteThread();
	
	// 关闭用户心跳线程
	void closeSmallHeartThread();

	// 关闭日志线程
	void closeLogThread();


	// 关闭用户在线线程
	void closeUserOnlineThread();

}
