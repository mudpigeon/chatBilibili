package top.nino.core.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.user.UserCookieInfo;

/**
 * @author : nino
 * @date : 2024/2/5 19:27
 */
@Slf4j
public class CookieUtils {

    /**
     * 获取最低限度cookie
     * 1.去除空格
     * 2.按照";"划分
     * 3.循环拆出":" key value
     * 4.存入UserCookie、PublicDataConf
     */
    public static UserCookieInfo parseCookie(String cookieValue) {
        UserCookieInfo userCookieInfo = new UserCookieInfo();
        if(StringUtils.isBlank(cookieValue)) return userCookieInfo;

        // 去除所有空格
        cookieValue = cookieValue.replaceAll("\\s", "");
        int haveNum = 0;
        for (String string : cookieValue.split(";")) {
            if (string.contains("=")) {
                String[] strings = string.split("=");
                String key = strings[0];
                String value = strings.length >= 2 ? strings[1] : "";

                if ("DedeUserID".equals(key)) {
                    userCookieInfo.setDedeUserID(value);
                    haveNum++;
                }

                if ("bili_jct".equals(key)) {
                    userCookieInfo.setBili_jct(value);
                    haveNum++;
                }

                if ("DedeUserID__ckMd5".equals(key)) {
                    userCookieInfo.setDedeUserID__ckMd5(value);
                    haveNum++;
                }

                if ("sid".equals(key)) {
                    userCookieInfo.setSid(value);
                    haveNum++;
                }

                if ("SESSDATA".equals(key)) {
                    userCookieInfo.setSESSDATA(value);
                    haveNum++;
                }
            }
        }
        if (haveNum >= 2) {
            userCookieInfo.setValidFlag(true);
            log.info("cookie装载成功,userCookie:{}", JSON.toJSONString(userCookieInfo));
            return userCookieInfo;
        }
        log.info("cookie参数异常，无法解析：{}", cookieValue);
        return userCookieInfo;
    }
}
