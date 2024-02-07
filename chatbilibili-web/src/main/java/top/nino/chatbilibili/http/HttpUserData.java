package top.nino.chatbilibili.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.login.QrCodeInfo;
import top.nino.api.model.user.UserBag;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.api.model.user.UserMedal;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.api.model.user_in_room_barrageMsg.UserBarrageMsg;
import top.nino.chatbilibili.tool.CurrencyTools;
import top.nino.core.CookieUtils;
import top.nino.core.JodaTimeUtils;
import top.nino.core.OkHttp3Utils;
import top.nino.core.UrlUtils;
import top.nino.service.http.HttpBilibiliServer;


import java.util.*;

@Slf4j
public class HttpUserData {


    /**
     * 初始化 获取用户信息+判断是否登陆状态
     */
    public static void httpGetUser() {
        String data = null;
        JSONObject jsonObject = null;
        Map<String, String> headers = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        try {
            data = OkHttp3Utils.getHttp3Utils().httpGet("https://account.bilibili.com/home/USERInfo", headers, null)
                    .body().string();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        if (data == null)
            return;
        jsonObject = JSONObject.parseObject(data);
        short code = jsonObject.getShort("code");
        if (code == 0) {
            log.info("已经登录:" + jsonObject.toString());
        } else if (code == -101) {
            log.info("未登录:" + jsonObject.toString());
        } else {
            log.error("未知错误,原因未知" + jsonObject.toString());
        }
    }



    public static QrCodeInfo httpGenerateQrcode() {
        String data = null;
        JSONObject jsonObject = null;
        QrCodeInfo qrCodeInfo = null;
        Map<String, String> headers = null;
        headers = new HashMap<>(3);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("Referer", "https://www.bilibili.com/");
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("https://passport.bilibili.com/x/passport-login/web/qrcode/generate?source=main-fe-header", headers, null).body().string();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        if (data == null)
            return qrCodeInfo;
        jsonObject = JSONObject.parseObject(data);
        short code = jsonObject.getShort("code");
        if (code == 0) {
            qrCodeInfo = JSONObject.parseObject(jsonObject.getString("data"), QrCodeInfo.class);
        } else {
            log.error("获取二维码失败,未知错误,原因未知" + jsonObject.toString());
        }
        return qrCodeInfo;
    }


    /**
     * HTTP 二维码轮询
     * 86101 未扫
     * 86090 扫了未确认
     *
     * @param key 钥匙
     * @return {@link String}
     */
    public static String httpQrcodePoll(String key) {
        String data = null;
        JSONObject jsonObject = null;
        Response response = null;

        Map<String, String> headers = new HashMap<>(3);

        Map<String, String> params = null;

        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("Referer", "https://www.bilibili.com/");

        params = new HashMap<>(3);
        params.put("qrcode_key", key);
        params.put("source", "main-fe-header");
        try {
            response = OkHttp3Utils.getHttp3Utils()
                    .httpGet("https://passport.bilibili.com/x/passport-login/web/qrcode/poll", headers, params);
            data = response.body().string();
            if (JSONObject.parseObject(data).getJSONObject("data").getIntValue("code")==0) {
                Headers headers2 = response.headers();
                List<String> cookies = headers2.values("Set-Cookie");
                Set<String> cookieSet = new HashSet<>();
                for (String string : cookies) {
                    cookieSet.add(string.substring(0, string.indexOf(";")));
                }
                StringBuilder stringBuilder = new StringBuilder(100);
                Iterator<String> iterable = cookieSet.iterator();
                while (iterable.hasNext()) {
                    stringBuilder.append(iterable.next());
                    if (iterable.hasNext()) {
                        stringBuilder.append(";");
                    }
                }
                GlobalSettingConf.COOKIE_VALUE = stringBuilder.toString();
                if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                    //处理token
                    UserCookieInfo userCookieInfo = CookieUtils.parseCookie(GlobalSettingConf.COOKIE_VALUE);
                    if(userCookieInfo.isValidFlag()) {
                        GlobalSettingConf.USER_COOKIE_INFO = userCookieInfo;
                    } else {
                        log.info("cookie解析异常");
                        return data;
                    }
                    //房间号非空则去获取用户弹幕长度
                    if (GlobalSettingConf.ROOM_ID != null) {
                        GlobalSettingConf.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
                        GlobalSettingConf.USER_MANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
                    }
                    log.info("扫码登录成功");
                }
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
            data = null;
        }
        return data;
    }


    /**
     * 获取用户在目标房间所能发送弹幕的最大长度
     */
    public static UserBarrageMsg httpGetUserBarrageMsg(Long roomId) {
        String data = null;
        JSONObject jsonObject = null;
        Map<String, String> headers = null;
        headers = new HashMap<>(4);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("referer", "https://live.bilibili.com/" + roomId);
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
        }
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByUser?room_id="
                            + roomId, headers, null)
                    .body().string();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        if (data == null)
            return null;
        jsonObject = JSONObject.parseObject(data);
        short code = jsonObject.getShort("code");
        if (code == 0) {
            log.info("获取本房间可发送弹幕长度成功");
            UserBarrageMsg barrageMsg = JSONObject
                    .parseObject((((JSONObject) jsonObject.get("data")).getString("property")), UserBarrageMsg.class);
            return barrageMsg;
        } else if (code == -101) {
            log.info("未登录，请登录:" + jsonObject.toString());
        } else if (code == -400) {
            log.info("房间号不存在或者未输入房间号:" + jsonObject.toString());
        } else {
            log.error("未知错误,原因未知" + jsonObject.toString());
        }
        return null;
    }

    /**
     * 发送弹幕
     *
     * @param msg 弹幕信息
     * @return
     */
    public static Short httpPostSendBarrage(String msg) {
        JSONObject jsonObject = null;
        String data = null;
        short code = -1;
        Map<String, String> headers = null;
        Map<String, String> params = null;
        if (GlobalSettingConf.USER_BARRAGE_MESSAGE == null || GlobalSettingConf.USER_COOKIE_INFO == null)
            return code;
        headers = new HashMap<>(4);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
        }
        if(StringUtils.isBlank(msg)){
            log.error("发送弹幕失败,原因:弹幕非空");
            return -400;
        }
        params = new HashMap<>(10);
        params.put("color", GlobalSettingConf.USER_BARRAGE_MESSAGE.getDanmu().getColor().toString());
        params.put("fontsize", "25");
        params.put("mode", GlobalSettingConf.USER_BARRAGE_MESSAGE.getDanmu().getMode().toString());
        params.put("msg", UrlUtils.URLEncoderString(msg,"utf-8"));
        params.put("rnd", String.valueOf(System.currentTimeMillis()).substring(0, 10));
        params.put("roomid", GlobalSettingConf.ROOM_ID.toString());
        params.put("bubble", GlobalSettingConf.USER_BARRAGE_MESSAGE.getBubble().toString());
        params.put("csrf_token", GlobalSettingConf.USER_COOKIE_INFO.getBili_jct());
        params.put("csrf", GlobalSettingConf.USER_COOKIE_INFO.getBili_jct());
        try {
            data = OkHttp3Utils.getHttp3Utils().httpPostForm("https://api.live.bilibili.com/msg/send", headers, params)
                    .body().string();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        if (data == null)
            return code;
        jsonObject = JSONObject.parseObject(data);
//		System.out.println(jsonObject.toJSONString().toString());
        if (jsonObject != null) {
            code = jsonObject.getShort("code");
            if (code == 0) {
                if (StringUtils.isBlank(jsonObject.getString("message").trim())) {
//				log.info("发送弹幕成功");
                } else if (jsonObject.getString("message").equals("msg in 1s")
                        || jsonObject.getString("message").equals("msg repeat")) {
                    log.info("发送弹幕失败，尝试重新发送" + jsonObject.getString("message"));
                    GlobalSettingConf.barrageString.add(msg);
                    synchronized (GlobalSettingConf.sendBarrageThread) {
                        GlobalSettingConf.sendBarrageThread.notify();
                    }
                } else {
                    log.info(jsonObject.toString());
                    String message = jsonObject.getString("message");
                    if("f".equals(message)||"k".equals(message)) message="触发破站关键字，请检查发送弹幕是否含有破站屏蔽词或者非法词汇";
                    log.error("发送弹幕失败,原因:" + message);
                    code = -402;
                }
            } else if (code == -111) {
                log.error("发送弹幕失败,原因:" + jsonObject.getString("message"));
            } else if (code == -500) {
                log.error("发送弹幕失败,原因:" + jsonObject.getString("message"));
            } else if (code == 11000) {
                log.error("发送弹幕失败,原因:弹幕含有关键字或者弹幕颜色不存在:" + jsonObject.getString("message"));
            } else {
                log.error("发送弹幕失败,未知错误,原因未知" + jsonObject.toString());
            }
        } else {
            return code;
        }
        return code;
    }

    /**
     * 送礼
     *
     * @param userBag 用户包
     * @param ruid    ruid
     * @param roomid  roomid
     * @return {@link Short}
     */
    public static Short httpPostSendBag(UserBag userBag,long ruid,long roomid) {
        JSONObject jsonObject = null;
        String data = null;
        short code = -1;
        Map<String, String> headers = null;
        Map<String, String> params = null;
        if (GlobalSettingConf.USER_COOKIE_INFO == null)
            return code;
//		if (PublicDataConf.USER.getUid() == recId)
//			return code;
        headers = new HashMap<>(4);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("referer", "https://live.bilibili.com/");
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
        }
        params = new HashMap<>(17);
        params.put("uid", String.valueOf(GlobalSettingConf.USER.getUid()));
        params.put("gift_id", String.valueOf(userBag.getGift_id()));
        params.put("ruid", String.valueOf(ruid));
        params.put("send_ruid", "0");
        params.put("gift_num",  String.valueOf(userBag.getGift_num()));
        params.put("bag_id", String.valueOf(userBag.getBag_id()));
        params.put("platform", "pc");
        params.put("biz_code", "Live");
        params.put("biz_id", String.valueOf(roomid));
        params.put("rnd", String.valueOf(JodaTimeUtils.getTimestamp()));
        params.put("metadata", "");
        params.put("price", "0");
        params.put("csrf_token", GlobalSettingConf.USER_COOKIE_INFO.getBili_jct());
        params.put("csrf", GlobalSettingConf.USER_COOKIE_INFO.getBili_jct());
        params.put("visit_id", "");
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpPostForm("https://api.live.bilibili.com/xlive/revenue/v1/gift/sendBag", headers, params).body()
                    .string();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        if (data == null)
            return code;
        jsonObject = JSONObject.parseObject(data);
        code = jsonObject.getShort("code");
        if (code == 0) {
            // 发送私聊成功
            log.info("赠送礼物成功,赠送房间:{},赠送主播id:{},送出礼物:{},个数:{},亲密度:{}",roomid,ruid,userBag.getGift_name(),userBag.getGift_num(),userBag.getFeed()*userBag.getGift_num());
        } else {
            log.error("赠送礼物失败,未知错误,原因未知" + jsonObject.toString());
        }
//        log.info("赠送礼物成功,赠送房间:{},赠送主播:{},送出礼物:{},个数:{},亲密度:{}",roomid,ruid,userBag.getGift_name(),userBag.getGift_num(),userBag.getFeed()*userBag.getGift_num());
        return 1;
    }

    /**
     * 禁言
     *
     * @param uid  被禁言人uid
     * @param hour 禁言时间 单位小时
     * @return
     */
    public static Short httpPostAddBlock(long uid, short hour) {
        JSONObject jsonObject = null;
        String data = null;
        short code = -1;
        Map<String, String> headers = null;
        Map<String, String> params = null;
        if (hour < 1) {
            hour = 1;
        }
        if (hour > 720) {
            hour = 720;
        }
        if (GlobalSettingConf.USER_COOKIE_INFO == null)
            return code;
        headers = new HashMap<>(4);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
        }
        params = new HashMap<>(7);
        params.put("roomid", GlobalSettingConf.ROOM_ID.toString());
        params.put("block_uid", String.valueOf(uid));
        params.put("hour", String.valueOf(hour));
        params.put("csrf_token", GlobalSettingConf.USER_COOKIE_INFO.getBili_jct());
        params.put("csrf", GlobalSettingConf.USER_COOKIE_INFO.getBili_jct());
        params.put("visit_id", "");
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpPostForm("https://api.live.bilibili.com/banned_service/v2/Silent/add_block_user", headers,
                            params)
                    .body().string();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        if (data == null)
            return code;
        jsonObject = JSONObject.parseObject(data);
        code = jsonObject.getShort("code");
        if (code == 0) {
            // 禁言成功
//			System.out.println(jsonObject.getString("data"));
        } else {
            log.error("禁言失败,原因" + jsonObject.getString("msg"));
        }
        return code;
    }





    public static List<UserMedal> httpGetMedalList() {
        String data = null;
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        List<UserMedal> userMedals = new ArrayList<>();
        short code = -1;
        Map<String, String> headers = null;
        Map<String, String> params = null;
        headers = new HashMap<>(3);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
        }
        params = new HashMap<>(3);
        try {
            int nowPage = 1;
            while (true) {
                params.put("page", String.valueOf(nowPage));
                params.put("page_size", "10");
                data = OkHttp3Utils.getHttp3Utils()
                        .httpGet("https://api.live.bilibili.com/xlive/app-ucenter/v1/user/GetMyMedals", headers, params)
                        .body().string();
                if (data == null)
                    return null;
                jsonObject = JSONObject.parseObject(data);
                code = jsonObject.getShort("code");
                if (code == 0) {
                    int totalPage = jsonObject.getJSONObject("data").getJSONObject("page_info").getInteger("total_page");
                    if (totalPage != 0) {
                        jsonArray = jsonObject.getJSONObject("data").getJSONArray("items");
                        if (jsonArray != null) {
                            List<UserMedal> userMedalList = jsonArray.toJavaList(UserMedal.class);
                            userMedals.addAll(userMedalList);
                        }
                    }
                    if(nowPage==totalPage){
                        break;
                    }
                } else {
                    log.error("获取勋章失败，原因：" + jsonObject.toString());
                    break;
                }
                nowPage++;
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        return userMedals;
    }

    //https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list

    public static List<UserBag> httpGetBagList(Long roomid) {
        String data = null;
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        short code = -1;
        Map<String, String> headers = null;
        Map<String, String> params = null;
        headers = new HashMap<>(3);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            headers.put("cookie", GlobalSettingConf.COOKIE_VALUE);
        }
        params = new HashMap<>(3);
        params.put("t", String.valueOf(JodaTimeUtils.getcurrMills()));
        params.put("room_id", String.valueOf(roomid));
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list", headers, params)
                    .body().string();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error(String.valueOf(e));
            data = null;
        }
        if (data == null)
            return null;
        jsonObject = JSONObject.parseObject(data);
        code = jsonObject.getShort("code");
        if (code == 0) {
            jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
            if (jsonArray != null) {
                List<UserBag> userBagList = jsonArray.toJavaList(UserBag.class);
                return userBagList;
            }
        } else {
            log.error("获取礼物包失败，原因：" + jsonObject.toString());
        }
        return null;
    }

}
