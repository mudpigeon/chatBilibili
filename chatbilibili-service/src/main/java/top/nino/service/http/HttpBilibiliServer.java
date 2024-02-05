package top.nino.service.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import top.nino.api.model.http.HttpBibilibiliUrl;
import top.nino.api.model.user.User;
import top.nino.core.HttpConstructUtil;
import top.nino.core.OkHttp3Utils;

/**
 * @author : nino
 * @date : 2024/2/5 20:45
 */
@Slf4j
public class HttpBilibiliServer {

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
        }

        if (responseJsonObject.getShort("code") == -500) {
            log.info("未登录，请登录:{}", responseJsonObject);
        }

        log.error("未知错误,原因未知:{}" , responseJsonObject);
        return user;

    }

}
