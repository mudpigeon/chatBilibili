package top.nino.chatbilibili.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import top.nino.api.model.tools.RequestHeaderTools;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.conf.base.CenterSetConf;
import top.nino.chatbilibili.conf.set.*;
import top.nino.chatbilibili.http.HttpUserData;
import top.nino.core.BASE64Encoder;
import top.nino.core.ProFileTools;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
@RequiredArgsConstructor
public class DanmujiInitService {
    private static final Logger LOGGER = LogManager.getLogger(DanmujiInitService.class);// 日志记录对象应是线程安全的

    // 借助lombok的RequiredArgsConstructor自动实现构造函数,此处由Spring利用构造函数完成自动装配
    private final SetServiceImpl checkService;


    /**
     * 初始化配置.如果有配置Cookie,则会以关联用户身份根据配置执行相关操作(每日签到,自动打卡
     */
    public void init() {
        Map<String, String> profileMap = new ConcurrentHashMap<>();
        String cookieString = null;
        BASE64Encoder base64Encoder = new BASE64Encoder();
        // 读取本地cookie
        try {
            profileMap.putAll(ProFileTools.read(GlobalSettingConf.PROFILE_NAME));
            cookieString = !StringUtils.isEmpty(profileMap.get(GlobalSettingConf.PROFILE_COOKIE_NAME))
                    ? new String(base64Encoder.decode(profileMap.get(GlobalSettingConf.PROFILE_COOKIE_NAME)))
                    : null;
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error("获取本地cookie失败,请重新登录" + e);
        }
        if (StringUtils.isNotBlank(cookieString)&&StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)) {
            GlobalSettingConf.COOKIE_VALUE = cookieString;
        }
        // 方法名未体现数据装载目的,但实际做了装载动作>_<
        // 检查登录状态
        HttpUserData.httpGetUserInfo();
        if (GlobalSettingConf.USER == null) {
            GlobalSettingConf.COOKIE_VALUE = null;
        } else {
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                profileMap.put(GlobalSettingConf.PROFILE_COOKIE_NAME, base64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
        }

        // 装载本地配置集
        // centerSetConf 第一次装配
        if (StringUtils.isNotBlank(profileMap.get(GlobalSettingConf.PROFILE_SET_NAME))) {
            GlobalSettingConf.centerSetConf = CenterSetConf.of(profileMap.get(GlobalSettingConf.PROFILE_SET_NAME));

            if (GlobalSettingConf.centerSetConf.getRoomid() != null && GlobalSettingConf.centerSetConf.getRoomid() > 0)
                GlobalSettingConf.ROOMID_LONG = GlobalSettingConf.centerSetConf.getRoomid();
            else if (GlobalSettingConf.ROOMID_LONG != null && GlobalSettingConf.ROOMID_LONG > 0)
                GlobalSettingConf.centerSetConf.setRoomid(GlobalSettingConf.ROOMID_LONG);

            if (GlobalSettingConf.centerSetConf.getAdvert() == null) {
                GlobalSettingConf.centerSetConf.setAdvert(new AdvertSetConf());
            }
            if (GlobalSettingConf.centerSetConf.getFollow() == null) {
                GlobalSettingConf.centerSetConf.setFollow(new ThankFollowSetConf());
            }
            if (GlobalSettingConf.centerSetConf.getThank_gift() == null) {
                GlobalSettingConf.centerSetConf.setThank_gift(new ThankGiftSetConf());
            }
            if (GlobalSettingConf.centerSetConf.getReply() == null) {
                GlobalSettingConf.centerSetConf.setReply(new AutoReplySetConf());
            }
            if (GlobalSettingConf.centerSetConf.getClock_in() == null) {
                GlobalSettingConf.centerSetConf.setClock_in(new ClockInSetConf(false, "签到"));
            }
            if (GlobalSettingConf.centerSetConf.getWelcome() == null) {
                GlobalSettingConf.centerSetConf.setWelcome(new ThankWelcomeSetConf());
            }
            if (GlobalSettingConf.centerSetConf.getAuto_gift() == null) {
                GlobalSettingConf.centerSetConf.setAuto_gift(new AutoSendGiftConf());
            }
            if (GlobalSettingConf.centerSetConf.getPrivacy() == null) {
                GlobalSettingConf.centerSetConf.setPrivacy(new PrivacySetConf());
            }
            if (GlobalSettingConf.centerSetConf.getBlack() == null) {
                GlobalSettingConf.centerSetConf.setBlack(new BlackListSetConf());
            }
        } else {
            // 无效的本地配置集则初始化一份
            // 此处无参初始化可以采用聚合根的思想
            GlobalSettingConf.centerSetConf = new CenterSetConf(new ThankGiftSetConf(), new AdvertSetConf(),
                    new ThankFollowSetConf(), new AutoReplySetConf(), new ClockInSetConf(), new ThankWelcomeSetConf(),
                    new AutoSendGiftConf(), new PrivacySetConf(), new BlackListSetConf());
        }

        //初始化配置文件结束
        profileMap.put(GlobalSettingConf.PROFILE_SET_NAME, base64Encoder.encode(GlobalSettingConf.centerSetConf.toJson().getBytes()));
        ProFileTools.write(profileMap, GlobalSettingConf.PROFILE_NAME);
        // 下方解析操作逻辑冗余, 可以清除
        try {
            // centerSetConf 第二次装配, 第一次与第二次转配期间profileMap的set属性装载仅依赖PublicDataConf.centerSetConf
            GlobalSettingConf.centerSetConf = CenterSetConf.of(profileMap.get(GlobalSettingConf.PROFILE_SET_NAME));
            LOGGER.info("读取配置文件成功");
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error("读取配置文件失败" + e);
        }

        // 分离cookie  改
        if (StringUtils.isNotEmpty(GlobalSettingConf.COOKIE_VALUE) && GlobalSettingConf.USER_COOKIE_INFO == null) {
            int controlNum = 0;
            String cookies = GlobalSettingConf.COOKIE_VALUE;
            GlobalSettingConf.USER_COOKIE_INFO = new UserCookieInfo();
            // This cookie should is `cookies`, because it collects some cookies.
            // 这里对Cookie簇的拆解属于Http基础设施, 不属于特定业务场景, 不应该在该方法中显示出现;
            // 解析Cookie簇的基础设施放入{@link RequestHeaderTools#parseCookies}中
            // 业务场景下只获取相应的Cookie来做些事情.
            Map<String, String> cookieKeyValue = RequestHeaderTools.parseCookies(cookies);

            if (cookieKeyValue.containsKey("DedeUserID")) {
                GlobalSettingConf.USER_COOKIE_INFO.setDedeUserID(cookieKeyValue.get("DedeUserID"));
                controlNum++;
            }
            if (cookieKeyValue.containsKey("bili_jct")) {
                GlobalSettingConf.USER_COOKIE_INFO.setBili_jct(cookieKeyValue.get("bili_jct"));
                controlNum++;
            }
            if (cookieKeyValue.containsKey("DedeUserID__ckMd5")) {
                GlobalSettingConf.USER_COOKIE_INFO.setDedeUserID__ckMd5(cookieKeyValue.get("DedeUserID__ckMd5"));
                controlNum++;
            }
            if (cookieKeyValue.containsKey("sid")) {
                GlobalSettingConf.USER_COOKIE_INFO.setSid(cookieKeyValue.get("sid"));
                controlNum++;
            }
            if (cookieKeyValue.containsKey("SESSDATA")) {
                GlobalSettingConf.USER_COOKIE_INFO.setSESSDATA(cookieKeyValue.get("SESSDATA"));
                controlNum++;
            }

            if (controlNum >= 2) {
                LOGGER.info("用户cookie装载成功");
            } else {
                LOGGER.info("用户cookie装载失败");
                GlobalSettingConf.USER_COOKIE_INFO = null;
            }
            checkService.holdSet(GlobalSettingConf.centerSetConf);
        }
    }
}
