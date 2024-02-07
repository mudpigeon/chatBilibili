package top.nino.chatbilibili.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.AllSettingConfig;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.core.data.BASE64Utils;
import top.nino.core.http.CookieUtils;
import top.nino.core.file.LocalGlobalSettingFileUtils;
import top.nino.service.http.HttpBilibiliServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : nino
 * @date : 2024/2/5 20:17
 */
@Slf4j
@Service
public class GlobalSettingFileServiceImpl implements GlobalSettingFileService {


    @Autowired
    private ThreadService threadService;

    /**
     * 创建或加载全局配置 还包含验证cookie合法性
     * @return 返回配置是否加载成功
     */
    @Override
    public boolean createAndValidateCookieAndLoadAndWrite() {

        Map<String, String> localGlobalSettingMap = new HashMap<>();

        // 先去加载本地文件的配置
        try {
            localGlobalSettingMap.putAll(LocalGlobalSettingFileUtils.readFile(GlobalSettingCache.GLOBAL_SETTING_FILE_NAME));
        } catch (IOException e) {
            log.error("加载本地文件异常", e);
        }

        // 如果有标识，则说明可以解密获得cookieSTring
        if(StringUtils.isNotBlank(localGlobalSettingMap.get(GlobalSettingCache.FILE_COOKIE_PREFIX))) {
            String cookieString = null;
            try {
                cookieString = new String(BASE64Utils.decode(localGlobalSettingMap.get(GlobalSettingCache.FILE_COOKIE_PREFIX)));
            } catch (Exception e) {
                log.error("获取本地cookie失败,请重新登录", e);
            }

            if (StringUtils.isNotBlank(cookieString) && StringUtils.isBlank(GlobalSettingCache.COOKIE_VALUE)) {
                // 说明直接从本地读到的cookie字符串，并且用户没有登录行为，那么就放入全局配置中
                GlobalSettingCache.COOKIE_VALUE = cookieString;
            }
        }

        GlobalSettingCache.USER = HttpBilibiliServer.httpGetUserInfo(GlobalSettingCache.COOKIE_VALUE);
        if(GlobalSettingCache.USER != null) {
            // 说明cookie仍然有效, 把cookie有效标识 放入Map
            GlobalSettingCache.USER_COOKIE_INFO = CookieUtils.parseCookie(GlobalSettingCache.COOKIE_VALUE);
            localGlobalSettingMap.put(GlobalSettingCache.FILE_COOKIE_PREFIX, BASE64Utils.encode(GlobalSettingCache.COOKIE_VALUE.getBytes()));
        } else {
            // 说明cookie失效，移除缓存
            GlobalSettingCache.clearUserCache();
        }

        if(StringUtils.isBlank(localGlobalSettingMap.get(GlobalSettingCache.FILE_SETTING_PREFIX))) {
            // 第一次，则要 直接新建默认配置
            GlobalSettingCache.ALL_SETTING_CONF = new AllSettingConfig();
        } else {
            // 可以从本地 加载 配置
            GlobalSettingCache.ALL_SETTING_CONF = AllSettingConfig.of(localGlobalSettingMap.get(GlobalSettingCache.FILE_SETTING_PREFIX));

            // 有直播间信息的话去加载
            if (GlobalSettingCache.ALL_SETTING_CONF.getRoomId() != null && GlobalSettingCache.ALL_SETTING_CONF.getRoomId() > 0) {
                GlobalSettingCache.ROOMID_LONG = GlobalSettingCache.ALL_SETTING_CONF.getRoomId();
            }
            if (GlobalSettingCache.ROOMID_LONG != null && GlobalSettingCache.ROOMID_LONG > 0) {
                GlobalSettingCache.ALL_SETTING_CONF.setRoomId(GlobalSettingCache.ROOMID_LONG);
            }
        }

        // 把所有的默认配置也放入缓存
        localGlobalSettingMap.put(GlobalSettingCache.FILE_SETTING_PREFIX, BASE64Utils.encode(GlobalSettingCache.ALL_SETTING_CONF.toJson().getBytes()));

        // 将缓存写到本地
        LocalGlobalSettingFileUtils.writeFile(GlobalSettingCache.GLOBAL_SETTING_FILE_NAME, localGlobalSettingMap);

        return true;
    }

    /**
     * 根据当前的配置
     * 运行处理弹幕的线程
     */
    @Override
    public void startReceiveDanmuThread() {

        synchronized (GlobalSettingCache.ALL_SETTING_CONF) {

            if (ObjectUtils.isEmpty(GlobalSettingCache.ROOM_ID) || GlobalSettingCache.ROOM_ID <= 0) {
                // 这些配置都是跟直播间相关的，所以必须设置直播间
                return;
            }

            if (ObjectUtils.isEmpty(GlobalSettingCache.bilibiliWebSocketProxy) || !GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
                return;
            }

            // 到了这里说明 已经连接到了 一个直播间
            // 所以直接启动弹幕接收
            threadService.startParseMessageThread();

            // 是否使用记录日志线程
            if (GlobalSettingCache.ALL_SETTING_CONF.is_log()) {
                threadService.startLogThread();
            } else {
                threadService.closeLogThread();
            }
        }
    }



}
