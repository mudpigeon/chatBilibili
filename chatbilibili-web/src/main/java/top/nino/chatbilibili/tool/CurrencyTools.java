package top.nino.chatbilibili.tool;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import top.nino.api.model.danmu.DanmuByteDataHandle;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.client.utils.ParseWebsocketMessageUtils;
import top.nino.core.ByteUtils;
import top.nino.core.JodaTimeUtils;

import java.util.*;


@Slf4j
public class CurrencyTools {

    /**
     * @param time
     * @return
     */
    public static String getGapTime(long time) {
        long hours = time / (1000 * 60 * 60);
        long minutes = (time - hours * (1000 * 60 * 60)) / (1000 * 60);
        long second = (time - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
        String diffTime = "";
        if (minutes < 10) {
            diffTime = hours + ":0" + minutes;
        } else {
            diffTime = hours + ":" + minutes;
        }
        if (second < 10) {
            diffTime = diffTime + ":0" + second;
        } else {
            diffTime = diffTime + ":" + second;
        }
        return diffTime;
    }

    /**
     * 获取心跳包byte[]
     *
     * @return
     */
    public static byte[] heartBytes() {
        return ByteUtils.byteMerger(
                ParseWebsocketMessageUtils.BEhandle(DanmuByteDataHandle.getBarrageHeadHandle(
                        "[object Object]".getBytes().length + 16, GlobalSettingConf.PACKAGE_HEAD_LENGTH,
                        GlobalSettingConf.PACKAGE_VERSION, GlobalSettingConf.heartPackageType, GlobalSettingConf.packageOther)),
                "[object Object]".getBytes());
    }

    /**
     * 生成uuid 8-4-4-4-12
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return 返回MD5
     */
    public static String deviceHash() {
        String hashString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()+-";
        char[] hashChars = hashString.toCharArray();
        StringBuilder stringBuilder = new StringBuilder(50);
        stringBuilder.append(System.currentTimeMillis()).append(hashChars[(int) (Math.random() * hashChars.length)])
                .append(hashChars[(int) (Math.random() * hashChars.length)])
                .append(hashChars[(int) (Math.random() * hashChars.length)])
                .append(hashChars[(int) (Math.random() * hashChars.length)])
                .append(hashChars[(int) (Math.random() * hashChars.length)]);
        return DigestUtils.md5Hex(stringBuilder.toString());
    }

    /**
     * 过滤房间号
     *
     * @return
     */
    public static long parseRoomId() {
        if (GlobalSettingConf.SHORT_ROOM_ID != null && GlobalSettingConf.SHORT_ROOM_ID > 0) {
            return GlobalSettingConf.SHORT_ROOM_ID;
        }
        return 0;
    }


    public static String dateToCron(Date date) {
        return JodaTimeUtils.format(date, "ss mm HH * * ?");
    }

    public static String dateStringToCron(String dateStr) {
        return JodaTimeUtils.format(JodaTimeUtils.parse(dateStr, "HH:mm:ss"), "ss mm HH * * ?");
    }


}
