package top.nino.core.websocket.parse;


import top.nino.api.model.danmu.Gift;
import top.nino.api.model.danmu.Guard;
import top.nino.core.time.JodaTimeUtils;

/**
 * @author : nino
 * @date : 2024/2/7 06:20
 */
public class ParseDanmuGuardUtils {


    public static String parseGuardDanmuContent(Guard guard) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JodaTimeUtils.formatDateTime(guard.getStart_time() * 1000));
        stringBuilder.append(":有人上船:");
        stringBuilder.append(guard.getUsername());
        stringBuilder.append("在本房间开通了");
        stringBuilder.append(guard.getNum());
        stringBuilder.append("个月");
        stringBuilder.append(guard.getGift_name());
        return stringBuilder.toString();
    }

    public static Gift parseGuardDanmuToGiftClass(Guard guard) {
        Gift gift = new Gift();
        gift.setGiftName(guard.getGift_name());
        gift.setNum(guard.getNum());
        gift.setPrice(guard.getPrice());
        gift.setTotal_coin((long) guard.getNum() * guard.getPrice());
        gift.setTimestamp(guard.getStart_time());
        gift.setAction("赠送");
        gift.setCoin_type((short) 1);
        gift.setUname(guard.getUsername());
        gift.setUid(guard.getUid());
        return gift;
    }
}
