package top.nino.chatbilibili.tool;


import org.apache.commons.lang3.StringUtils;

public class ParseIndentityTools {



	/**
	 * 醒目留言时间++
	 *
	 * @param time
	 * @return
	 */
	public static Integer parseTime(Integer time) {
		if (time != null && time.toString().endsWith("9")) {
			return time + 1;
		}
		return time;
	}

	/**
	 * 过滤金银瓜子类型
	 * @param coin_type
	 * @return
	 */
	public static short parseCoin_type(String coin_type) {
		if(StringUtils.isBlank(coin_type)) {
			return -1;
		}
		switch(coin_type.trim()) {
			case "silver":
				return 0;
			case "gold":
				return 1;
			default:
				return -1;
		}
	}
}
