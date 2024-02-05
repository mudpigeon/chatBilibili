package top.nino.chatbilibili.thread;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.enums.ListGiftShieldStatus;
import top.nino.api.model.enums.ListPeopleShieldStatus;
import top.nino.api.model.enums.ShieldGift;
import top.nino.api.model.enums.ThankGiftStatus;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.conf.base.ThankGiftRuleSet;
import top.nino.chatbilibili.tool.ParseSetStatusTools;
import top.nino.chatbilibili.tool.ShieldGiftTools;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

@Getter
@Setter
public class ParseThankGiftThread extends Thread {
//	@SuppressWarnings("unused")
//	private static Logger LOGGER = LogManager.getLogger(ParseThankGiftThread.class);
	public volatile boolean TFLAG = false;
	private Long delaytime = 3000L;
	private Long timestamp;
	private String thankGiftString = "感谢%uName%大佬%Type%的%GiftName% x%Num%";
	private ThankGiftStatus thankGiftStatus;
	private Short num = 2;
	private HashSet<ThankGiftRuleSet> thankGiftRuleSets;
	private boolean is_num = true;

	private ListGiftShieldStatus listGiftShieldStatus;

	private ListPeopleShieldStatus listPeopleShieldStatus;



	private String somePeoplesHandle(Map<String, Vector<Gift>> hashMap, int max, String giftString) {
		int i = 1;
		StringBuilder stringBuilderName = new StringBuilder(150);
		StringBuilder stringBuilderGifts = new StringBuilder(200);
		for (Iterator<Entry<String, Vector<Gift>>> iterator = hashMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Vector<Gift>> entryMap = iterator.next();
			stringBuilderName.append(entryMap.getKey()).append(",");
			for (Gift gift : entryMap.getValue()) {
				if (is_num()) {
//				if(!stringBuilderGifts.toString().contains(gift.getGiftName())) {
					stringBuilderGifts.append(gift.getNum()).append("个").append(gift.getGiftName()).append(",");
//				}
				} else {
					if (!stringBuilderGifts.toString().contains(gift.getGiftName())) {
						stringBuilderGifts.append(gift.getGiftName()).append(",");
					}
				}
			}
			i++;
			iterator.remove();
			if (i > max) {
				break;
			}
		}
		stringBuilderGifts.delete(stringBuilderGifts.length() - 1, stringBuilderGifts.length());
		stringBuilderName.delete(stringBuilderName.length() - 1, stringBuilderName.length());
		giftString = StringUtils.replace(giftString, "%uNames%", stringBuilderName.toString());
		giftString = StringUtils.replace(giftString, "%Gifts%", stringBuilderGifts.toString());
		//如果还有这个参数写死赠送
		giftString = StringUtils.replace(giftString, "%Type%", "赠送");
		return giftString;
	}

	public String handleThankStr(String thankStr) {
		String thankGiftStrs[] = null;
		if (StringUtils.indexOf(thankStr, "\n") != -1) {
			thankGiftStrs = StringUtils.split(thankStr, "\n");
		}
		if (thankGiftStrs != null && thankGiftStrs.length > 1) {
			return thankGiftStrs[(int) Math.ceil(Math.random() * thankGiftStrs.length) - 1];
		}
		return thankStr;
	}


}
