package top.nino.chatbilibili.component;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.conf.base.CenterSetConf;
import top.nino.chatbilibili.conf.base.ThankGiftRuleSet;
import top.nino.chatbilibili.conf.set.*;
import top.nino.chatbilibili.http.HttpOtherData;
import top.nino.chatbilibili.thread.*;
import top.nino.chatbilibili.tool.ParseSetStatusTools;


import java.util.HashSet;
import java.util.Iterator;


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


	//关闭用户相关线程
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
	 * @param centerSetConf
	 * @return
	 */
	@Override
	public boolean startParseMessageThread(CenterSetConf centerSetConf) {

		HashSet<ThankGiftRuleSet> thankGiftRuleSets = new HashSet<>();

		if (GlobalSettingConf.parseMessageThread != null && !GlobalSettingConf.parseMessageThread.getState().toString().equals("TERMINATED")) {
			GlobalSettingConf.parseMessageThread.setCenterSetConf(centerSetConf);
			GlobalSettingConf.parseMessageThread.setThankGiftRuleSets(thankGiftRuleSets);
			return false;
		}

		GlobalSettingConf.parseMessageThread = new ParseMessageThread();
		GlobalSettingConf.parseMessageThread.FLAG = false;
		GlobalSettingConf.parseMessageThread.start();
		GlobalSettingConf.parseMessageThread.setCenterSetConf(centerSetConf);
		GlobalSettingConf.parseMessageThread.setThankGiftRuleSets(thankGiftRuleSets);
		if (GlobalSettingConf.parseMessageThread != null
				&& !GlobalSettingConf.parseMessageThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean startHeartByteThread() {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.heartByteThread != null) {
			return false;
		}
		GlobalSettingConf.heartByteThread = new HeartByteThread();
		GlobalSettingConf.heartByteThread.HFLAG = false;
		GlobalSettingConf.heartByteThread.start();
		if (GlobalSettingConf.heartByteThread != null
				&& !GlobalSettingConf.heartByteThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean startLogThread() {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.logThread != null) {
			return false;
		}
		GlobalSettingConf.logThread = new LogThread();
		GlobalSettingConf.logThread.FLAG = false;
		GlobalSettingConf.logThread.start();
		if (GlobalSettingConf.logThread != null && !GlobalSettingConf.logThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

//	@Override
//	public boolean startAdvertThread(CenterSetConf centerSetConf) {
//		// TODO 自动生成的方法存根
//		if (PublicDataConf.advertThread != null || StringUtils.isBlank(PublicDataConf.USERCOOKIE)) {
//			PublicDataConf.advertThread
//					.setAdvertStatus(ParseSetStatusTools.getAdvertStatus(centerSetConf.getAdvert().getStatus()));
//			PublicDataConf.advertThread.setTime(centerSetConf.getAdvert().getTime());
//			PublicDataConf.advertThread.setAdvertBarrage(centerSetConf.getAdvert().getAdverts());
//			return false;
//		}
//		PublicDataConf.advertThread = new AdvertThread();
//		PublicDataConf.advertThread.FLAG = false;
//		PublicDataConf.advertThread
//				.setAdvertStatus(ParseSetStatusTools.getAdvertStatus(centerSetConf.getAdvert().getStatus()));
//		PublicDataConf.advertThread.setTime(centerSetConf.getAdvert().getTime());
//		PublicDataConf.advertThread.setAdvertBarrage(centerSetConf.getAdvert().getAdverts());
//		PublicDataConf.advertThread.start();
//		startSendBarrageThread();
//		if (PublicDataConf.advertThread != null
//				&& !PublicDataConf.advertThread.getState().toString().equals("TERMINATED")) {
//			return true;
//		}
//		return false;
//	}

	@Override
	public boolean startAdvertThread(AdvertSetConf advertSetConf) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.advertThread != null || StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)) {
			GlobalSettingConf.advertThread
					.setAdvertStatus(ParseSetStatusTools.getAdvertStatus(advertSetConf.getStatus()));
			GlobalSettingConf.advertThread.setTime(advertSetConf.getTime());
			GlobalSettingConf.advertThread.setAdvertBarrage(advertSetConf.getAdverts());
			return false;
		}
		GlobalSettingConf.advertThread = new AdvertThread();
		GlobalSettingConf.advertThread.FLAG = false;
		GlobalSettingConf.advertThread
				.setAdvertStatus(ParseSetStatusTools.getAdvertStatus(advertSetConf.getStatus()));
		GlobalSettingConf.advertThread.setTime(advertSetConf.getTime());
		GlobalSettingConf.advertThread.setAdvertBarrage(advertSetConf.getAdverts());
		GlobalSettingConf.advertThread.start();
		startSendBarrageThread();
		if (GlobalSettingConf.advertThread != null
				&& !GlobalSettingConf.advertThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

//	@Override
//	public boolean startAutoReplyThread(CenterSetConf centerSetConf) {
//		// TODO 自动生成的方法存根
//		HashSet<AutoReplySet> autoReplySets = new HashSet<AutoReplySet>();
//		for (Iterator<AutoReplySet> iterator = centerSetConf.getReply().getAutoReplySets().iterator(); iterator
//				.hasNext();) {
//			AutoReplySet autoReplySet = iterator.next();
//			if (autoReplySet.is_open()) {
//				autoReplySets.add(autoReplySet);
//			}
//		}
//		if (PublicDataConf.autoReplyThread != null || StringUtils.isBlank(PublicDataConf.USERCOOKIE)) {
//			PublicDataConf.autoReplyThread.setTime(centerSetConf.getReply().getTime());
//			PublicDataConf.autoReplyThread.setAutoReplySets(autoReplySets);
//			return false;
//		}
//		PublicDataConf.autoReplyThread = new AutoReplyThread();
//		PublicDataConf.autoReplyThread.FLAG = false;
//		PublicDataConf.autoReplyThread.setTime(centerSetConf.getReply().getTime());
//		PublicDataConf.autoReplyThread.setAutoReplySets(autoReplySets);
//		PublicDataConf.autoReplyThread.start();
//		startSendBarrageThread();
//		if (PublicDataConf.autoReplyThread != null
//				&& !PublicDataConf.autoReplyThread.getState().toString().equals("TERMINATED")) {
//			return true;
//		}
//		return false;
//	}

	@Override
	public boolean startAutoReplyThread(AutoReplySetConf autoReplySetConf) {
		// TODO 自动生成的方法存根
		HashSet<AutoReplySet> autoReplySets = new HashSet<AutoReplySet>();
		for (Iterator<AutoReplySet> iterator = autoReplySetConf.getAutoReplySets().iterator(); iterator
				.hasNext();) {
			AutoReplySet autoReplySet = iterator.next();
			if (autoReplySet.is_open()) {
				autoReplySets.add(autoReplySet);
			}
		}
		if (GlobalSettingConf.autoReplyThread != null || StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)) {
			GlobalSettingConf.autoReplyThread.setTime(autoReplySetConf.getTime());
			GlobalSettingConf.autoReplyThread.setAutoReplySets(autoReplySets);
			return false;
		}
		GlobalSettingConf.autoReplyThread = new AutoReplyThread();
		GlobalSettingConf.autoReplyThread.FLAG = false;
		GlobalSettingConf.autoReplyThread.setTime(autoReplySetConf.getTime());
		GlobalSettingConf.autoReplyThread.setAutoReplySets(autoReplySets);
		GlobalSettingConf.autoReplyThread.start();
		startSendBarrageThread();
		if (GlobalSettingConf.autoReplyThread != null
				&& !GlobalSettingConf.autoReplyThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean startSendBarrageThread() {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.sendBarrageThread != null || StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)) {
			return false;
		}
		GlobalSettingConf.sendBarrageThread = new SendBarrageThread();
		GlobalSettingConf.sendBarrageThread.FLAG = false;
		GlobalSettingConf.sendBarrageThread.start();
		if (GlobalSettingConf.sendBarrageThread != null
				&& !GlobalSettingConf.sendBarrageThread.getState().toString().equals("TERMINATED")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean startUserOnlineThread() {
		// TODO 自动生成的方法存根
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

	@Override
	public boolean startGiftShieldThread(String giftName, int time) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.parsethankGiftThread.getState().toString().equals("TERMINATED")
				|| GlobalSettingConf.parsethankGiftThread.getState().toString().equals("NEW")) {
			GlobalSettingConf.giftShieldThread = new GiftShieldThread();
			GlobalSettingConf.giftShieldThread.FLAG = false;
			GlobalSettingConf.giftShieldThread.setGiftName(giftName);
			GlobalSettingConf.giftShieldThread.setTime(time);
			GlobalSettingConf.giftShieldThread.start();
			return true;
		}
		return false;
	}

	@Override
	public boolean startFollowShieldThread(int time) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.parsethankFollowThread.getState().toString().equals("TERMINATED")
				|| GlobalSettingConf.parsethankFollowThread.getState().toString().equals("NEW")) {
			GlobalSettingConf.followShieldThread = new FollowShieldThread();
			GlobalSettingConf.followShieldThread.FLAG = false;
			GlobalSettingConf.followShieldThread.setTime(time);
			GlobalSettingConf.followShieldThread.start();
			return true;
		}
		return false;
	}

	@Override
	public boolean startWelcomeShieldThread(int time) {
		if (GlobalSettingConf.parseThankWelcomeThread.getState().toString().equals("TERMINATED")
				|| GlobalSettingConf.parseThankWelcomeThread.getState().toString().equals("NEW")) {
			GlobalSettingConf.welcomeShieldThread = new WelcomeShieldThread();
			GlobalSettingConf.welcomeShieldThread.FLAG = false;
			GlobalSettingConf.welcomeShieldThread.setTime(time);
			GlobalSettingConf.welcomeShieldThread.start();
			return true;
		}
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
	public void startParseThankFollowThread(ThankFollowSetConf thankFollowSetConf) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.parsethankFollowThread.getState().toString().equals("TERMINATED")
				|| GlobalSettingConf.parsethankFollowThread.getState().toString().equals("NEW")) {
			GlobalSettingConf.parsethankFollowThread = new ParseThankFollowThread();
			GlobalSettingConf.parsethankFollowThread.setDelaytime((long) (1000 * thankFollowSetConf.getDelaytime()));
			GlobalSettingConf.parsethankFollowThread.start();
			GlobalSettingConf.parsethankFollowThread.setTimestamp(System.currentTimeMillis());
			GlobalSettingConf.parsethankFollowThread.setThankFollowString(thankFollowSetConf.getFollows());
			GlobalSettingConf.parsethankFollowThread.setNum(thankFollowSetConf.getNum());
		} else {
			GlobalSettingConf.parsethankFollowThread.setTimestamp(System.currentTimeMillis());
			GlobalSettingConf.parsethankFollowThread.setThankFollowString(thankFollowSetConf.getFollows());
			GlobalSettingConf.parsethankFollowThread.setNum(thankFollowSetConf.getNum());
		}
	}

	@Override
	public void startParseThankWelcomeThread(ThankWelcomeSetConf thankWelcomeSetConf) {
		if (GlobalSettingConf.parseThankWelcomeThread.getState().toString().equals("TERMINATED")
				|| GlobalSettingConf.parseThankWelcomeThread.getState().toString().equals("NEW")) {
			GlobalSettingConf.parseThankWelcomeThread = new ParseThankWelcomeThread();
			GlobalSettingConf.parseThankWelcomeThread.setDelaytime((long) (1000 * thankWelcomeSetConf.getDelaytime()));
			GlobalSettingConf.parseThankWelcomeThread.start();
			GlobalSettingConf.parseThankWelcomeThread.setTimestamp(System.currentTimeMillis());
			GlobalSettingConf.parseThankWelcomeThread.setThankWelcomeString(thankWelcomeSetConf.getWelcomes());
			GlobalSettingConf.parseThankWelcomeThread.setNum(thankWelcomeSetConf.getNum());
		} else {
			GlobalSettingConf.parseThankWelcomeThread.setTimestamp(System.currentTimeMillis());
			GlobalSettingConf.parseThankWelcomeThread.setThankWelcomeString(thankWelcomeSetConf.getWelcomes());
			GlobalSettingConf.parseThankWelcomeThread.setNum(thankWelcomeSetConf.getNum());
		}
	}

	@Override
	public void setParseMessageThread(
			CenterSetConf centerSetConf) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.parseMessageThread != null) {
			HashSet<ThankGiftRuleSet> thankGiftRuleSets = new HashSet<>();
			// thankGiftRuleSets
//			for (Iterator<ThankGiftRuleSet> iterator = centerSetConf.getThank_gift().getThankGiftRuleSets()
//					.iterator(); iterator.hasNext();) {
//				ThankGiftRuleSet thankGiftRuleSet = iterator.next();
//				if (thankGiftRuleSet.is_open()) {
//					thankGiftRuleSets.add(thankGiftRuleSet);
//				}
//			}
			GlobalSettingConf.parseMessageThread.setCenterSetConf(centerSetConf);
			GlobalSettingConf.parseMessageThread.setThankGiftRuleSets(thankGiftRuleSets);
		}
	}

//	@Override
//	public void setAdvertThread(CenterSetConf centerSetConf) {
//		// TODO 自动生成的方法存根
//		if (PublicDataConf.advertThread != null) {
//			PublicDataConf.advertThread
//					.setAdvertStatus(ParseSetStatusTools.getAdvertStatus(centerSetConf.getAdvert().getStatus()));
//			PublicDataConf.advertThread.setTime(centerSetConf.getAdvert().getTime());
//			PublicDataConf.advertThread.setAdvertBarrage(centerSetConf.getAdvert().getAdverts());
//		}
//	}

	@Override
	public void setAdvertThread(AdvertSetConf advertSetConf) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.advertThread != null) {
			GlobalSettingConf.advertThread
					.setAdvertStatus(ParseSetStatusTools.getAdvertStatus(advertSetConf.getStatus()));
			GlobalSettingConf.advertThread.setTime(advertSetConf.getTime());
			GlobalSettingConf.advertThread.setAdvertBarrage(advertSetConf.getAdverts());
		}
	}

//	@Override
//	public void setAutoReplyThread(CenterSetConf centerSetConf) {
//		// TODO 自动生成的方法存根
//		if (PublicDataConf.autoReplyThread != null) {
//			HashSet<AutoReplySet> autoReplySets = new HashSet<AutoReplySet>();
//			for (Iterator<AutoReplySet> iterator = centerSetConf.getReply().getAutoReplySets().iterator(); iterator
//					.hasNext();) {
//				AutoReplySet autoReplySet = iterator.next();
//				if (autoReplySet.is_open()) {
//					autoReplySets.add(autoReplySet);
//				}
//			}
//			PublicDataConf.autoReplyThread.setTime(centerSetConf.getReply().getTime());
//			PublicDataConf.autoReplyThread.setAutoReplySets(autoReplySets);
//		}
//	}

	@Override
	public void setAutoReplyThread(AutoReplySetConf autoReplySetConf) {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.autoReplyThread != null) {
			HashSet<AutoReplySet> autoReplySets = new HashSet<AutoReplySet>();
			for (Iterator<AutoReplySet> iterator = autoReplySetConf.getAutoReplySets().iterator(); iterator
					.hasNext();) {
				AutoReplySet autoReplySet = iterator.next();
				if (autoReplySet.is_open()) {
					autoReplySets.add(autoReplySet);
				}
			}
			GlobalSettingConf.autoReplyThread.setTime(autoReplySetConf.getTime());
			GlobalSettingConf.autoReplyThread.setAutoReplySets(autoReplySets);
		}
	}

	@Override
	public void closeParseMessageThread() {
		if (GlobalSettingConf.parseMessageThread != null) {
			GlobalSettingConf.parseMessageThread.FLAG = true;
			GlobalSettingConf.parseMessageThread.interrupt();
			GlobalSettingConf.parseMessageThread = null;
		}
	}

	@Override
	public void closeHeartByteThread() {
		if (GlobalSettingConf.heartByteThread != null) {
			GlobalSettingConf.heartByteThread.HFLAG = true;
			GlobalSettingConf.heartByteThread.interrupt();
			GlobalSettingConf.heartByteThread = null;
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
		if (GlobalSettingConf.advertThread != null) {
			GlobalSettingConf.advertThread.FLAG = true;
			GlobalSettingConf.advertThread.interrupt();
			GlobalSettingConf.advertThread = null;
		}
	}

	@Override
	public void closeAutoReplyThread() {
		if (GlobalSettingConf.autoReplyThread != null) {
			GlobalSettingConf.autoReplyThread.FLAG = true;
			GlobalSettingConf.autoReplyThread.interrupt();
			GlobalSettingConf.autoReplyThread = null;
		}
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
		if (GlobalSettingConf.giftShieldThread != null
				&& !GlobalSettingConf.giftShieldThread.getState().toString().equals("TERMINATED")) {
			GlobalSettingConf.giftShieldThread.FLAG = false;
			GlobalSettingConf.giftShieldThread.interrupt();
		}
	}

	@Override
	public void closeFollowShieldThread() {
		// TODO 自动生成的方法存根
		if (GlobalSettingConf.followShieldThread != null
				&& !GlobalSettingConf.followShieldThread.getState().toString().equals("TERMINATED")) {
			GlobalSettingConf.followShieldThread.FLAG = false;
			GlobalSettingConf.followShieldThread.interrupt();
		}
	}

	@Override
	public void closeWelcomeShieldThread() {
		if (GlobalSettingConf.welcomeShieldThread != null
				&& !GlobalSettingConf.welcomeShieldThread.getState().toString().equals("TERMINATED")) {
			GlobalSettingConf.welcomeShieldThread.FLAG = false;
			GlobalSettingConf.welcomeShieldThread.interrupt();
		}
	}

	@Override
	public void closeSmallHeartThread() {
		// TODO 自动生成的方法存根
		if(GlobalSettingConf.smallHeartThread!=null) {
			GlobalSettingConf.smallHeartThread.FLAG=true;
			GlobalSettingConf.smallHeartThread.interrupt();
			GlobalSettingConf.smallHeartThread=null;
		}
	}

}
