package top.nino.core.websocket.parse;


import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.danmu.Guard;
import top.nino.api.model.danmu.RedPackage;
import top.nino.api.model.superchat.SuperChat;
import top.nino.core.time.JodaTimeUtils;

/**
 * @author : nino
 * @date : 2024/2/7 06:20
 */
public class ParseDanmuRedPackageUtils {


    public static String parseRedPackageDanmeContent(RedPackage redPackage) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(JodaTimeUtils.formatDateTime(redPackage.getStart_time() * 1000));
        stringBuilder.append(":收到红包:");
        stringBuilder.append(redPackage.getUname());
        stringBuilder.append(" ");
        stringBuilder.append(redPackage.getAction());
        stringBuilder.append("的:");
        stringBuilder.append(redPackage.getGift_name());
        stringBuilder.append(" x ");
        stringBuilder.append(redPackage.getNum());
        return stringBuilder.toString();
    }


    public static Gift parseRedPackageDanmuToGiftClass(RedPackage redPackage) {
        Gift gift = new Gift();
        gift.setGiftName(redPackage.getGift_name());
        gift.setNum(redPackage.getNum());
        gift.setPrice(redPackage.getPrice());
        gift.setTotal_coin((long) redPackage.getNum() * redPackage.getPrice());
        gift.setTimestamp(redPackage.getStart_time());
        gift.setAction(redPackage.getAction());
        gift.setCoin_type((short) 1);
        gift.setUname(redPackage.getUname());
        gift.setUid(redPackage.getUid());
        gift.setMedal_info(redPackage.getMedal_info());
        return gift;
    }

}
