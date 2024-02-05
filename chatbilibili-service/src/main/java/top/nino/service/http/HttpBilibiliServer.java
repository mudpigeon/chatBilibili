package top.nino.service.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.http.HttpBibilibiliUrl;
import top.nino.api.model.http.HttpHeader;
import top.nino.api.model.user.User;
import top.nino.core.HttpConstructUtil;
import top.nino.core.OkHttp3Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : nino
 * @date : 2024/2/5 20:45
 */
@Slf4j
public class HttpBilibiliServer {

    /**
     * 退出 删除cookie
     */
    public static void loginOut(String cookieValue) {
        try {
            OkHttp3Utils.getHttp3Utils().httpGet(
                    HttpBibilibiliUrl.GET_LOGIN_OUT,
                    HttpConstructUtil.constructHeader(cookieValue, HttpHeader.REFER_BILIBILI),
                    null);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }

    }
        /**
         * 获取用户信息 需要cookie 初始化
         */
    public static User httpGetUserInfo(String cookieValue) {
        String responseString;
        User user = null;

        try {
            responseString = OkHttp3Utils.getHttp3Utils().httpGet(
                    HttpBibilibiliUrl.GET_USER_INFO,
                    HttpConstructUtil.constructHeader(cookieValue),
                    null).body().string();
        } catch (Exception e) {
            log.error(String.valueOf(e));
            responseString = null;
        }

        if (responseString == null) {
            return user;
        }

        JSONObject responseJsonObject = JSONObject.parseObject(responseString);

        if (responseJsonObject.getString("code").equals("REPONSE_OK")) {
            user = JSONObject.parseObject(responseJsonObject.getString("data"), User.class);
            log.info("已经登录，用户信息为:{}", JSON.toJSONString(user));
            return user;
        }

        if (responseJsonObject.getShort("code") == -500) {
            log.info("未登录，请登录:{}", responseJsonObject);
            return user;
        }

        log.error("未知错误,原因未知:{}" , responseJsonObject);
        return user;
    }

}
