package top.nino.chatbilibili.component;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.conf.base.AllSettingConfig;
import top.nino.chatbilibili.conf.base.ThankGiftRuleSet;
import top.nino.chatbilibili.conf.set.*;
import top.nino.chatbilibili.thread.*;
import top.nino.chatbilibili.tool.ParseSetStatusTools;


import java.util.HashSet;
import java.util.Iterator;


/**
 * @author cengzhongjie
 */
@Component
public class ThreadComponentImpl implements ThreadComponent {

	//关闭全部线程
	public void closeAll(){
		closeHeartByteThread();
		closeUserOnlineThread();
		closeAdvertThread();
		closeSendBarrageThread();
		closeLogThread();
		closeGiftShieldThread();
		closeFollowShieldThread();
		closeWelcomeShieldThread();
		closeAutoReplyThread();
		closeSmallHeartThread();
		closeParseMessageThread();
	}


	// 关闭用户相关线程
	@Override
	public void closeUser(boolean close){

		closeUserOnlineThread();
		closeSendBarrageThread();
		closeLogThread();
		closeSmallHeartThread();
		if (close) {
			closeHeartByteThread();
			closeParseMessageThread();
		}

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
		// TODO 自动生成的方法存根
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
	public void startAdvertThread(AdvertSetConf advertSetConf) {
	}


	@Override
	public void startAutoReplyThread(AutoReplySetConf autoReplySetConf) {
		// TODO 自动生成的方法存根
		HashSet<AutoReplySet> autoReplySets = new HashSet<AutoReplySet>();
		for (Iterator<AutoReplySet> iterator = autoReplySetConf.getAutoReplySets().iterator(); iterator
				.hasNext();) {
			AutoReplySet autoReplySet = iterator.next();
			if (autoReplySet.is_open()) {
				autoReplySets.add(autoReplySet);
			}
		}

		startSendBarrageThread();

	}

	@Override
	public void startSendBarrageThread() {
		if (GlobalSettingConf.sendBarrageThread != null || StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)) {
			return;
		}
		GlobalSettingConf.sendBarrageThread = new SendBarrageThread();
		GlobalSettingConf.sendBarrageThread.FLAG = false;
		GlobalSettingConf.sendBarrageThread.start();
		if (GlobalSettingConf.sendBarrageThread != null
				&& !GlobalSettingConf.sendBarrageThread.getState().toString().equals("TERMINATED")) {
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
		// TODO 自动生成的方法存根
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


	public void startParseThankGiftThread(ThankGiftSetConf thankGiftSetConf,
										  HashSet<ThankGiftRuleSet> thankGiftRuleSets) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.parsethankGiftThread == null) {
			GlobalSettingConf.parsethankGiftThread = new ParseThankGiftThread();
		}
		if (GlobalSettingConf.parsethankGiftThread.getState().toString().equals("TERMINATED")
				|| GlobalSettingConf.parsethankGiftThread.getState().toString().equals("NEW")) {
			GlobalSettingConf.parsethankGiftThread = new ParseThankGiftThread();
			GlobalSettingConf.parsethankGiftThread.setDelaytime((long) (1000 * thankGiftSetConf.getDelaytime()));
			GlobalSettingConf.parsethankGiftThread.start();
			GlobalSettingConf.parsethankGiftThread.setTimestamp(System.currentTimeMillis());
			GlobalSettingConf.parsethankGiftThread.setThankGiftString(thankGiftSetConf.getThank());
			GlobalSettingConf.parsethankGiftThread
					.setThankGiftStatus(ParseSetStatusTools.getThankGiftStatus(thankGiftSetConf.getThank_status()));
			GlobalSettingConf.parsethankGiftThread.setThankGiftRuleSets(thankGiftRuleSets);
			GlobalSettingConf.parsethankGiftThread.setNum(thankGiftSetConf.getNum());
			GlobalSettingConf.parsethankGiftThread.set_num(thankGiftSetConf.is_num());
			GlobalSettingConf.parsethankGiftThread.setListGiftShieldStatus(
					ParseSetStatusTools.getListGiftShieldStatus(thankGiftSetConf.getList_gift_shield_status()));
			GlobalSettingConf.parsethankGiftThread.setListPeopleShieldStatus(
					ParseSetStatusTools.getListPeopleShieldStatus(thankGiftSetConf.getList_people_shield_status()));
		} else {
			GlobalSettingConf.parsethankGiftThread.setTimestamp(System.currentTimeMillis());
			GlobalSettingConf.parsethankGiftThread.setThankGiftString(thankGiftSetConf.getThank());
			GlobalSettingConf.parsethankGiftThread
					.setThankGiftStatus(ParseSetStatusTools.getThankGiftStatus(thankGiftSetConf.getThank_status()));
			GlobalSettingConf.parsethankGiftThread.setThankGiftRuleSets(thankGiftRuleSets);
			GlobalSettingConf.parsethankGiftThread.setNum(thankGiftSetConf.getNum());
			GlobalSettingConf.parsethankGiftThread.set_num(thankGiftSetConf.is_num());
			GlobalSettingConf.parsethankGiftThread.setListGiftShieldStatus(
					ParseSetStatusTools.getListGiftShieldStatus(thankGiftSetConf.getList_gift_shield_status()));
			GlobalSettingConf.parsethankGiftThread.setListPeopleShieldStatus(
					ParseSetStatusTools.getListPeopleShieldStatus(thankGiftSetConf.getList_people_shield_status()));
		}
	}


	@Override
	public void setParseMessageThread(AllSettingConfig allSettingConfig) {
		if (GlobalSettingConf.parseDanmuMessageThread != null) {

		}
	}


	public void setAutoReplyThread(AutoReplySetConf autoReplySetConf) {

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
	public void closeAdvertThread() {

	}

	@Override
	public void closeAutoReplyThread() {

	}

	@Override
	public void closeSendBarrageThread() {
		if (GlobalSettingConf.sendBarrageThread != null) {
			GlobalSettingConf.sendBarrageThread.FLAG = true;
			GlobalSettingConf.sendBarrageThread.interrupt();
			GlobalSettingConf.sendBarrageThread = null;
		}
	}

	@Override
	public void closeGiftShieldThread() {

	}

	@Override
	public void closeFollowShieldThread() {

	}

	@Override
	public void closeWelcomeShieldThread() {

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
