package top.nino.chatbilibili.tool;

import top.nino.api.model.enums.*;
import top.nino.chatbilibili.conf.base.AllSettingConfig;


import java.util.concurrent.ConcurrentHashMap;


public class ParseSetStatusTools {
	public static ShieldGift getGiftShieldStatus(short code) {
		switch (code) {
			case 0:
				return ShieldGift.OPTIONAL;
			case 1:
				return ShieldGift.SILVER;
			case 2:
				return ShieldGift.HIGH_PRICE;
			case 3:
				return ShieldGift.CUSTOM_RULE;
			default:
				return ShieldGift.OPTIONAL;
		}
	}

	public static ThankGiftStatus getThankGiftStatus(short code) {
		switch (code) {
			case 0:
				return ThankGiftStatus.one_people;
			case 1:
				return ThankGiftStatus.some_people;
			case 2:
				return ThankGiftStatus.some_peoples;
			default:
				return ThankGiftStatus.one_people;
		}
	}

	public static AdvertStatus getAdvertStatus(short code) {
		switch (code) {
			case 0:
				return AdvertStatus.deafult;
			case 1:
				return AdvertStatus.random;
			default:
				return AdvertStatus.deafult;
		}
	}

	public static ListGiftShieldStatus getListGiftShieldStatus(short code) {
		switch (code) {
			case 0:
				return ListGiftShieldStatus.BLACK;
			case 1:
				return ListGiftShieldStatus.WHITE;
			default:
				return ListGiftShieldStatus.BLACK;
		}
	}


	public static ListPeopleShieldStatus getListPeopleShieldStatus(short code) {
		switch (code) {
			case 0:
				return ListPeopleShieldStatus.ALL;
			case 1:
				return ListPeopleShieldStatus.MEDAL;
			case 2:
				return ListPeopleShieldStatus.GUARD;
			default:
				return ListPeopleShieldStatus.ALL;
		}
	}

	/**
	 * 等待移除 2.4.9
	 */
	@Deprecated
	public static ConcurrentHashMap<ShieldMessage, Boolean> getMessageConcurrentHashMap(AllSettingConfig allSettingConfig,
																						short live_status) {
		ConcurrentHashMap<ShieldMessage, Boolean> messageConcurrentHashMap = new ConcurrentHashMap<ShieldMessage, Boolean>(
				18);
		if (allSettingConfig.is_barrage_guard()) {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_guard, true);
		} else {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_guard, false);
		}
		if(allSettingConfig.is_cmd()) {
			messageConcurrentHashMap.put(ShieldMessage.is_cmd, true);
		}else {
			messageConcurrentHashMap.put(ShieldMessage.is_cmd, false);
		}
		if (allSettingConfig.is_barrage_vip()) {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_vip, true);
		} else {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_vip, false);
		}
		if (allSettingConfig.is_barrage_manager()) {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_manager, true);
		} else {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_manager, false);
		}
		if (allSettingConfig.is_barrage_medal()) {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_medal, true);
		} else {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_medal, false);
		}
		if (allSettingConfig.is_barrage_ul()) {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_ul, true);
		} else {
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_ul, false);
		}
		if(allSettingConfig.is_barrage_anchor_shield()){
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_anchor_shield,true);
		}else{
			messageConcurrentHashMap.put(ShieldMessage.is_barrage_anchor_shield,false);
		}


		if (allSettingConfig.is_gift()) {
			messageConcurrentHashMap.put(ShieldMessage.is_gift, true);
		} else {
			messageConcurrentHashMap.put(ShieldMessage.is_gift, false);
		}


		return messageConcurrentHashMap;
	}

	public static AllSettingConfig initCenterChildConfig(AllSettingConfig allSettingConfig){
		return allSettingConfig;
	}
}
