package top.nino.core.websocket.parse;


import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.danmu.Guard;
import top.nino.api.model.superchat.SuperChat;
import top.nino.core.time.JodaTimeUtils;

/**
 * @author : nino
 * @date : 2024/2/7 06:20
 */
public class ParseDanmuSuperChatUtils {


    public static String parseSuperChatDanmeContent(SuperChat superChat) {

        superChat.setTime(parseTime(superChat.getTime()));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JodaTimeUtils.formatDateTime(superChat.getStart_time() * 1000));
        stringBuilder.append(":收到留言:");
        stringBuilder.append(superChat.getUser_info().getUname());
        stringBuilder.append(" 他用了");
        //适配6.11破站更新金瓜子为电池  叔叔真有你的
        stringBuilder.append(superChat.getPrice() * 10);
        stringBuilder.append("电池留言了");
        stringBuilder.append(superChat.getTime());
        stringBuilder.append("秒说: ");
        stringBuilder.append(superChat.getMessage());

        return stringBuilder.toString();
    }


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
