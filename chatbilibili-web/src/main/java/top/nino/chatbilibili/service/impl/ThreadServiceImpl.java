package top.nino.chatbilibili.service.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.AllSettingConfig;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.thread.*;


/**
 * @author nino
 */
@Component
public class ThreadServiceImpl implements ThreadService {

	//关闭全部线程
	public void closeAll(){
		closeHeartByteThread();
		closeUserOnlineThread();
		closeLogThread();
		closeSmallHeartThread();
		closeParseMessageThread();
	}


	// 关闭用户相关线程
	@Override
	public void closeUser(){

		closeUserOnlineThread();
		closeLogThread();
		closeSmallHeartThread();
		closeHeartByteThread();
		closeParseMessageThread();
	}

	/**
	 * 开启弹幕处理线程
	 *
	 */
	@Override
	public void startParseMessageThread() {


		if (ObjectUtils.isNotEmpty(GlobalSettingConf.parseDanmuMessageThread)  && !"TERMINATED".equals(GlobalSettingConf.parseDanmuMessageThread.getState().toString())) {
			return;
		}

		GlobalSettingConf.parseDanmuMessageThread = new ParseDanmuMessageThread();
		GlobalSettingConf.parseDanmuMessageThread.closeFlag = false;
		GlobalSettingConf.parseDanmuMessageThread.start();
		if (GlobalSettingConf.parseDanmuMessageThread != null
				&& !GlobalSettingConf.parseDanmuMessageThread.getState().toString().equals("TERMINATED")) {
		}
	}

	@Override
	public void startHeartCheckBilibiliDanmuServerThread() {
		if (ObjectUtils.isNotEmpty(GlobalSettingConf.heartCheckBilibiliDanmuServerThread)) {
			return;
		}
		// 没有心跳线程，就去启动
		GlobalSettingConf.heartCheckBilibiliDanmuServerThread = new HeartCheckBilibiliDanmuServerThread();
		GlobalSettingConf.heartCheckBilibiliDanmuServerThread.HFLAG = false;
		GlobalSettingConf.heartCheckBilibiliDanmuServerThread.start();

	}

	@Override
	public void startLogThread() {
		if (GlobalSettingConf.logThread != null) {
			return;
		}
		GlobalSettingConf.logThread = new LogThread();
		GlobalSettingConf.logThread.FLAG = false;
		GlobalSettingConf.logThread.start();
		if (GlobalSettingConf.logThread != null && !GlobalSettingConf.logThread.getState().toString().equals("TERMINATED")) {
		}
	}


	@Override
	public boolean startUserOnlineThread() {
		if (GlobalSettingConf.heartBeatThread != null || GlobalSettingConf.heartBeatsThread != null
				|| GlobalSettingConf.userOnlineHeartThread != null) {
			return false;
		}
		GlobalSettingConf.heartBeatThread = new HeartBeatThread();
		GlobalSettingConf.heartBeatThread.FLAG = false;
		GlobalSettingConf.heartBeatThread.start();

		GlobalSettingConf.heartBeatsThread = new HeartBeatsThread();
		GlobalSettingConf.heartBeatsThread.FLAG = false;
		GlobalSettingConf.heartBeatsThread.start();

		GlobalSettingConf.userOnlineHeartThread = new UserOnlineHeartThread();
		GlobalSettingConf.userOnlineHeartThread.FLAG = false;
		GlobalSettingConf.userOnlineHeartThread.start();

		if (GlobalSettingConf.heartBeatThread != null && GlobalSettingConf.heartBeatsThread != null
				&& GlobalSettingConf.userOnlineHeartThread != null
				&& !GlobalSettingConf.heartBeatThread.getState().toString().equals("TERMINATED")
				&& !GlobalSettingConf.heartBeatsThread.getState().toString().equals("TERMINATED")
				&& !GlobalSettingConf.userOnlineHeartThread.getState().toString().equals("TERMINATED")) {
			return true;
		} else {
			closeUserOnlineThread();
		}
		return false;
	}

	@Override
	public boolean startSmallHeartThread() {
		if (GlobalSettingConf.smallHeartThread != null
				&& !GlobalSettingConf.smallHeartThread.getState().toString().equals("TERMINATED")) {
			return false;
		}
		if(null== GlobalSettingConf.userOnlineHeartThread) {
			return false;
		}

		GlobalSettingConf.smallHeartThread = new SmallHeartThread();
		GlobalSettingConf.smallHeartThread.FLAG = false;
		GlobalSettingConf.smallHeartThread.start();
		if (GlobalSettingConf.smallHeartThread != null
				&& !GlobalSettingConf.smallHeartThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

	public boolean startGiftShieldThread(String giftName, int time) {

		return false;
	}

	@Override
	public boolean startFollowShieldThread(int time) {

		return false;
	}

	public boolean startWelcomeShieldThread(int time) {

		return false;
	}

	@Override
	public void closeUserOnlineThread() {
		if (GlobalSettingConf.userOnlineHeartThread != null) {
			GlobalSettingConf.userOnlineHeartThread.FLAG = true;
			GlobalSettingConf.userOnlineHeartThread.interrupt();
			GlobalSettingConf.userOnlineHeartThread = null;
		}
		if (GlobalSettingConf.heartBeatThread != null) {
			GlobalSettingConf.heartBeatThread.FLAG = true;
			GlobalSettingConf.heartBeatThread.interrupt();
			GlobalSettingConf.heartBeatThread = null;
		}
		if (GlobalSettingConf.heartBeatsThread != null) {
			GlobalSettingConf.heartBeatsThread.FLAG = true;
			GlobalSettingConf.heartBeatsThread.interrupt();
			GlobalSettingConf.heartBeatsThread = null;
		}
	}

	@Override
	public void setParseMessageThread(AllSettingConfig allSettingConfig) {
		if (GlobalSettingConf.parseDanmuMessageThread != null) {

		}
	}

	@Override
	public void closeParseMessageThread() {
		if (GlobalSettingConf.parseDanmuMessageThread != null) {
			GlobalSettingConf.parseDanmuMessageThread.closeFlag = true;
			GlobalSettingConf.parseDanmuMessageThread.interrupt();
			GlobalSettingConf.parseDanmuMessageThread = null;
		}
	}

	@Override
	public void closeHeartByteThread() {
		if (GlobalSettingConf.heartCheckBilibiliDanmuServerThread != null) {
			GlobalSettingConf.heartCheckBilibiliDanmuServerThread.HFLAG = true;
			GlobalSettingConf.heartCheckBilibiliDanmuServerThread.interrupt();
			GlobalSettingConf.heartCheckBilibiliDanmuServerThread = null;
		}
	}

	@Override
	public void closeLogThread() {
		if (GlobalSettingConf.logThread != null) {
			GlobalSettingConf.logThread.FLAG = true;
			GlobalSettingConf.logThread.interrupt();
			GlobalSettingConf.logThread = null;
		}
	}

	@Override
	public void closeSmallHeartThread() {
		if(GlobalSettingConf.smallHeartThread!=null) {
			GlobalSettingConf.smallHeartThread.FLAG=true;
			GlobalSettingConf.smallHeartThread.interrupt();
			GlobalSettingConf.smallHeartThread=null;
		}
	}

}
