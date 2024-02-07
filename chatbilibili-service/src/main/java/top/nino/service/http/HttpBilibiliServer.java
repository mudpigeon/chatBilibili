package top.nino.service.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.danmu.RoomInfo;
import top.nino.api.model.enums.ResponseCode;
import top.nino.api.model.http.HttpBibilibiliUrl;
import top.nino.api.model.http.HttpHeader;
import top.nino.api.model.login.QrCodeInfo;
import top.nino.api.model.room.AnchorMedalInfo;
import top.nino.api.model.room.RoomAnchorInfo;
import top.nino.api.model.room.RoomAllInfo;
import top.nino.api.model.room.RoomStatusInfo;
import top.nino.api.model.server.DanmuInfo;
import top.nino.api.model.user.User;
import top.nino.api.model.user.UserManager;
import top.nino.api.model.user_in_room_barrageMsg.UserBarrageMsg;
import top.nino.core.http.HttpHeaderConstructUtil;
import top.nino.core.http.OkHttp3Utils;

import java.util.*;

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
                    HttpHeaderConstructUtil.constructHeader(cookieValue, HttpHeader.REFER_BILIBILI),
                    null);
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }

    }

    /**
     * 获取用户信息 需要cookie 初始化
     */
    public static User httpGetUserInfo(String cookieValue) {
        User user = null;

        if (StringUtils.isBlank(cookieValue)) {
            return user;
        }

        String responseString;

        try {
            responseString = OkHttp3Utils.getHttp3Utils().httpGet(
                    HttpBibilibiliUrl.GET_USER_INFO,
                    HttpHeaderConstructUtil.constructHeader(cookieValue),
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

        log.error("未知错误,原因未知:{}", responseJsonObject);
        return user;
    }


    /**
     * 获取用户在目标房间所能发送弹幕的最大长度
     */
    public static UserBarrageMsg httpGetUserBarrageMsg(Long roomId, String cookieValue) {

        UserBarrageMsg userBarrageMsg = null;

        if (ObjectUtils.isEmpty(roomId) || roomId == 0) {
            // 如果没有直播间短号，直接结束
            return userBarrageMsg;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_KEY);
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_LIVE_BILIBILI + roomId);

        if (StringUtils.isNotBlank(cookieValue)) {
            headers.put("cookie", cookieValue);
        }

        String responseDataString;
        try {
            responseDataString = OkHttp3Utils.getHttp3Utils().httpGet(
                    HttpBibilibiliUrl.GET_PARAM_NONE_ROOM_ID + roomId,
                    headers,
                    null).body().string();
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return userBarrageMsg;
        }

        JSONObject responseJsonObject = JSONObject.parseObject(responseDataString);

        short code = responseJsonObject.getShort("code");
        if (code == 0) {
            userBarrageMsg = JSONObject.parseObject((((JSONObject) responseJsonObject.get("data")).getString("property")), UserBarrageMsg.class);
            log.info("获取本房间可发送弹幕长度成功");
            return userBarrageMsg;
        }

        if (code == -101) {
            log.info("未登录，请登录:{}", responseJsonObject.toString());
            return userBarrageMsg;
        }

        if (code == -400) {
            log.info("房间号不存在或者未输入房间号:{}", responseJsonObject.toString());
            return userBarrageMsg;
        }

        log.error("未知错误,原因未知:{}", responseJsonObject.toString());
        return userBarrageMsg;
    }


    /**
     * 获取用户在目标房间所能发送弹幕的最大长度
     */
    public static UserManager httpGetUserManagerMsg(Long roomId, Long shortRoomId, String cookieValue) {

        UserManager userManager = null;
        if (ObjectUtils.isEmpty(shortRoomId) || shortRoomId == 0) {
            // 如果没有直播间短号，直接结束
            return userManager;
        }

        Map<String, String> headers = new HashMap<>(4);
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_KEY);
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_LIVE_BILIBILI + shortRoomId);

        if (StringUtils.isNotBlank(cookieValue)) {
            headers.put("cookie", cookieValue);
        }

        String responseDataString = null;
        try {
            responseDataString = OkHttp3Utils.getHttp3Utils().httpGet(
                    HttpBibilibiliUrl.GET_PARAM_NONE_ROOM_ID + shortRoomId,
                    headers,
                    null).body().string();
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return userManager;
        }

        JSONObject responseJsonObject = JSONObject.parseObject(responseDataString);

        short code = responseJsonObject.getShort("code");
        if (code == 0) {
            Boolean manager = responseJsonObject.getJSONObject("data").getJSONObject("badge").getBoolean("is_room_admin");
            userManager = new UserManager();
            userManager.set_manager(manager != null ? manager : false);
            userManager.setRoomid(roomId);
            userManager.setShort_roomid(shortRoomId);
            log.info("获取本房间是否是管理员 成功");
            return userManager;
        }

        if (code == -101) {
            log.info("未登录，请登录:{}", responseJsonObject.toString());
            return userManager;
        }

        if (code == -400) {
            log.info("房间号不存在或者未输入房间号:{}", responseJsonObject.toString());
            return userManager;
        }

        log.error("未知错误,原因未知:{}", responseJsonObject.toString());
        return userManager;

    }


    public static QrCodeInfo httpGetQrcodeInfo() {

        QrCodeInfo qrCodeInfo = null;

        Map<String, String> headers = new HashMap<>(3);
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_BILIBILI);

        String data;
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet(HttpBibilibiliUrl.GET_QRCODE, headers, null).body().string();
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return qrCodeInfo;
        }

        JSONObject jsonObject = JSONObject.parseObject(data);
        short code = jsonObject.getShort("code");
        if (code == 0) {
            qrCodeInfo = JSONObject.parseObject(jsonObject.getString("data"), QrCodeInfo.class);
            log.info("获取二维码信息成功:{}", qrCodeInfo);
            return qrCodeInfo;
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
     * @param qrCodeKey 钥匙
     * @return {@link String}
     */
    public static top.nino.api.model.vo.Response httpCheckQrcodeScanStatus(String qrCodeKey) {

        top.nino.api.model.vo.Response myResponse = new top.nino.api.model.vo.Response();
        myResponse.setCode(ResponseCode.NONE_QRCODE_KEY_INFO.getCode());
        myResponse.setMsg(ResponseCode.NONE_QRCODE_KEY_INFO.getMsg());

        if (StringUtils.isBlank(qrCodeKey)) {
            return myResponse;
        }
        StringBuilder cookieValue = new StringBuilder();

        Map<String, String> headers = new HashMap<>(3);
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_BILIBILI);

        Map<String, String> requestParams = new HashMap<>(3);
        requestParams.put("qrcode_key", qrCodeKey);
        requestParams.put("source", "main-fe-header");

        try {
            Response response = OkHttp3Utils.getHttp3Utils()
                    .httpGet(HttpBibilibiliUrl.GET_QRCODE_SCAN_STATUS, headers, requestParams);
            String responseData = response.body().string();
            log.info("检查二维码扫描情况：{}", JSONObject.parseObject(responseData).getJSONObject("data"));
            if (JSONObject.parseObject(responseData).getJSONObject("data").getIntValue("code") == 0) {

                Headers responseHeader = response.headers();
                Set<String> cookieSet = new HashSet<>();

                responseHeader.values("Set-Cookie").forEach(cookie -> {
                    cookieSet.add(cookie.substring(0, cookie.indexOf(";")));
                });

                cookieSet.forEach(cookie -> {
                    cookieValue.append(cookie).append(";");
                });
                cookieValue.setLength(cookieValue.length() - 1);
                myResponse.setCode(ResponseCode.SUCCESS.getCode());
                myResponse.setMsg(ResponseCode.SUCCESS.getMsg());
                myResponse.setResult(cookieValue.toString());
                return myResponse;
            }

            if (JSONObject.parseObject(responseData).getJSONObject("data").getIntValue("code") == 86038) {
                myResponse.setCode(ResponseCode.QRCODE_UN_VALID_INFO.getCode());
                myResponse.setMsg(ResponseCode.QRCODE_UN_VALID_INFO.getMsg());
                return myResponse;

            }

            if (JSONObject.parseObject(responseData).getJSONObject("data").getIntValue("code") == 86101) {
                myResponse.setCode(ResponseCode.QRCODE_NO_SCAN_INFO.getCode());
                myResponse.setMsg(ResponseCode.QRCODE_NO_SCAN_INFO.getMsg());
                return myResponse;

            }

            if (JSONObject.parseObject(responseData).getJSONObject("data").getIntValue("code") == 86090) {
                myResponse.setCode(ResponseCode.QRCODE_NO_SECOND_CHECK_INFO.getCode());
                myResponse.setMsg(ResponseCode.QRCODE_NO_SECOND_CHECK_INFO.getMsg());
                return myResponse;
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
        myResponse.setCode(ResponseCode.QRCODE_UN_KNOW_INFO.getCode());
        myResponse.setMsg(ResponseCode.QRCODE_UN_KNOW_INFO.getMsg());
        return myResponse;
    }

    /**
     * 获取直播间状态信息
     *
     * @param roomId 直播间房间号
     */
    public static RoomStatusInfo httpGetRoomStatusInfo(Long roomId) {
        RoomStatusInfo roomStatusInfo = null;


        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);

        String responseDataString;
        try {
            responseDataString = OkHttp3Utils.getHttp3Utils()
                    .httpGet(HttpBibilibiliUrl.GET_ROOM_STATUS_INFO + roomId, headers, null).body()
                    .string();
        } catch (Exception e) {
            log.error("获取直播间状态信息错误", e);
            return roomStatusInfo;
        }
        if (ObjectUtils.isEmpty(responseDataString)) {
            return roomStatusInfo;
        }

        JSONObject responseJsonObject = JSONObject.parseObject(responseDataString);
        short code = responseJsonObject.getShort("code");
        if (code == 0) {
            roomStatusInfo = responseJsonObject.getObject("data", RoomStatusInfo.class);
        } else {
            log.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:{}", responseJsonObject.getString("message"));
        }
        return roomStatusInfo;
    }


    /**
     * 获取目标房间部分信息
     *
     * @param roomId
     * @return
     */
    public static RoomAnchorInfo httpGetRoomAnchorInfo(Long roomId, Long shortRoomId) {

        RoomAnchorInfo roomAnchorInfo = null;

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_BILIBILI + shortRoomId);

        String responseDataString;
        try {
            responseDataString = OkHttp3Utils.getHttp3Utils()
                    .httpGet(HttpBibilibiliUrl.GET_ROOM_ANCHOR_INFO + roomId,
                            headers, null)
                    .body().string();
        } catch (Exception e) {
            log.error("获取直播间和主播关联信息错误", e);
            return roomAnchorInfo;
        }

        JSONObject responseJsonObject = JSONObject.parseObject(responseDataString);
        short code = responseJsonObject.getShort("code");
        if (code == 0) {
            roomAnchorInfo = responseJsonObject.getObject("data", RoomAnchorInfo.class);
        } else {
            log.error("直播房间号不存在，或者未知错误，请尝试更换房间号,原因:" + responseJsonObject.getString("message"));
        }
        return roomAnchorInfo;
    }


    /**
     * 获取主播关注数
     *
     * @return 返回关注数
     */
    public static Long httpGetAnchorFanSum(Long anchorUid) {

        Long followersNum = 0L;

        if (ObjectUtils.isEmpty(anchorUid)) {
            return followersNum;
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_SPACE_BILIBILI + "{" + anchorUid + "}/");

        Map<String, String> requestData = new HashMap<>();
        requestData.put("vmid", anchorUid.toString());

        String responseDataString = null;
        try {
            responseDataString = OkHttp3Utils.getHttp3Utils().httpGet(HttpBibilibiliUrl.GET_ANCHOR_FAN_SUM, headers, requestData)
                    .body().string();
        } catch (Exception e) {
            log.error("获取获取主播关注数失败", e);
            return followersNum;
        }
        JSONObject responseJsonObject = JSONObject.parseObject(responseDataString);
        short code = responseJsonObject.getShort("code");
        if (code == 0) {
            followersNum = ((JSONObject) responseJsonObject.get("data")).getLong("follower");
        } else {
            log.error("获取关注数失败，请重试:{}", responseJsonObject.getString("message"));
        }
        return followersNum;
    }


    /**
     * 获取房间最详细信息 日后扩容 目前只是获取主播uid 改
     * @return
     */
    public static RoomAllInfo httpGetRoomAllInfo(Long shortRoomId) {

        RoomAllInfo roomAllInfo = new RoomAllInfo();

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_KEY);
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_LIVE_BILIBILI + shortRoomId);

        String responseDataString;
        try {
            responseDataString = OkHttp3Utils.getHttp3Utils()
                    .httpGet(HttpBibilibiliUrl.GET_ROOM_INFO + shortRoomId, headers, null)
                    .body().string();
        } catch (Exception e) {
            log.error("获取直播间信息失败", e);
            return roomAllInfo;
        }
        JSONObject responseJsonObject = JSONObject.parseObject(responseDataString);
        short code = responseJsonObject.getShort("code");

        AnchorMedalInfo anchorMedalInfo = null;
        RoomInfo roomInfo = null;

        if (code == 0) {
            roomInfo = JSON.parseObject(
                    ((JSONObject) responseJsonObject.get("data")).getString("room_info"),
                    RoomInfo.class);
            anchorMedalInfo = JSON.parseObject(responseJsonObject.getJSONObject("data").getJSONObject("anchor_info").getString("medal_info"),
                    AnchorMedalInfo.class);
        } else {
            log.error("获取房间详细信息失败，请稍后尝试:{}", responseJsonObject.getString("message"));
        }
        roomAllInfo.setRoomInfo(roomInfo);
        roomAllInfo.setAnchorMedalInfo(anchorMedalInfo);
        return roomAllInfo;
    }


    /**
     * 获取连接目标房间websocket端口 接口
     *
     * @return
     */
    public static DanmuInfo httpGetDanmuInfo(String cookieValue, Long roomId, Long shortRoomId) {
        DanmuInfo danmuInfo = null;

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.REFER_KEY, HttpHeader.REFER_LIVE_BILIBILI + shortRoomId);
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);
        if (StringUtils.isNotBlank(cookieValue)) {
            headers.put("cookie", cookieValue);
        }

        Map<String, String> requestData = new HashMap<>();
        requestData.put("id", roomId.toString());
        requestData.put("type", "0");

        String responseDataString;
        try {
            responseDataString = OkHttp3Utils.getHttp3Utils()
                    .httpGet(HttpBibilibiliUrl.GET_DANMU_INFO, headers, requestData)
                    .body().string();
        } catch (Exception e) {
            log.error("获取B站弹幕服务器地址异常", e);
            return danmuInfo;
        }

        JSONObject responseJsonObject = JSONObject.parseObject(responseDataString);
        short code = responseJsonObject.getShort("code");
        if (code == 0) {
            danmuInfo = responseJsonObject.getObject("data", DanmuInfo.class);
        } else {
            log.error("未知错误,原因:{}", responseJsonObject.getString("message"));
        }
        return danmuInfo;
    }
}
