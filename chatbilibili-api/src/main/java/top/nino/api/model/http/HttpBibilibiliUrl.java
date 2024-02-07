package top.nino.api.model.http;

/**
 * @author : nino
 * @date : 2024/2/5 20:58
 */
public class HttpBibilibiliUrl {
    public static final String GET_LOGIN_OUT = "https://passport.bilibili.com/login?act=exit";
    public static final String GET_PARAM_NONE_ROOM_ID = "https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByUser?room_id=";
    public static final String GET_USER_INFO = "https://api.live.bilibili.com/User/getUserInfo";
    public static final String GET_QRCODE = "https://passport.bilibili.com/x/passport-login/web/qrcode/generate?source=main-fe-header";
    public static final String GET_QRCODE_SCAN_STATUS = "https://passport.bilibili.com/x/passport-login/web/qrcode/poll";
    public static final String GET_ROOM_STATUS_INFO = "https://api.live.bilibili.com/room/v1/Room/room_init?id=";
    public static final String GET_ROOM_ANCHOR_INFO = "https://api.live.bilibili.com/room_ex/v1/RoomNews/get?roomid=";
    public static final String GET_ANCHOR_FAN_SUM = "https://api.bilibili.com/x/relation/stat";
    public static final String GET_ROOM_INFO = "https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id=";
    public static final String GET_DANMU_INFO = "https://api.live.bilibili.com/xlive/web-room/v1/index/getDanmuInfo";
}
