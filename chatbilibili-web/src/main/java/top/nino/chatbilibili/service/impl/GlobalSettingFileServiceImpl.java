package top.nino.chatbilibili.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingConf;
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
            localGlobalSettingMap.putAll(LocalGlobalSettingFileUtils.readFile(GlobalSettingConf.GLOBAL_SETTING_FILE_NAME));
        } catch (IOException e) {
            log.error("加载本地文件异常", e);
        }

        // 如果有标识，则说明可以解密获得cookieSTring
        if(StringUtils.isNotBlank(localGlobalSettingMap.get(GlobalSettingConf.FILE_COOKIE_PREFIX))) {
            String cookieString = null;
            try {
                cookieString = new String(BASE64Utils.decode(localGlobalSettingMap.get(GlobalSettingConf.FILE_COOKIE_PREFIX)));
            } catch (Exception e) {
                log.error("获取本地cookie失败,请重新登录", e);
            }

            if (StringUtils.isNotBlank(cookieString) && StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)) {
                // 说明直接从本地读到的cookie字符串，并且用户没有登录行为，那么就放入全局配置中
                GlobalSettingConf.COOKIE_VALUE = cookieString;
            }
        }

        GlobalSettingConf.USER = HttpBilibiliServer.httpGetUserInfo(GlobalSettingConf.COOKIE_VALUE);
        if(GlobalSettingConf.USER != null) {
            // 说明cookie仍然有效, 把cookie有效标识 放入Map
            GlobalSettingConf.USER_COOKIE_INFO = CookieUtils.parseCookie(GlobalSettingConf.COOKIE_VALUE);
            localGlobalSettingMap.put(GlobalSettingConf.FILE_COOKIE_PREFIX, BASE64Utils.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
        } else {
            // 说明cookie失效，移除缓存
            GlobalSettingConf.clearUserCache();
        }

        if(StringUtils.isBlank(localGlobalSettingMap.get(GlobalSettingConf.FILE_SETTING_PREFIX))) {
            // 第一次，则要 直接新建默认配置
            GlobalSettingConf.ALL_SETTING_CONF = new AllSettingConfig();
        } else {
            // 可以从本地 加载 配置
            GlobalSettingConf.ALL_SETTING_CONF = AllSettingConfig.of(localGlobalSettingMap.get(GlobalSettingConf.FILE_SETTING_PREFIX));

            // 有直播间信息的话去加载
            if (GlobalSettingConf.ALL_SETTING_CONF.getRoomId() != null && GlobalSettingConf.ALL_SETTING_CONF.getRoomId() > 0) {
                GlobalSettingConf.ROOMID_LONG = GlobalSettingConf.ALL_SETTING_CONF.getRoomId();
            }
            if (GlobalSettingConf.ROOMID_LONG != null && GlobalSettingConf.ROOMID_LONG > 0) {
                GlobalSettingConf.ALL_SETTING_CONF.setRoomId(GlobalSettingConf.ROOMID_LONG);
            }
        }

        // 把所有的默认配置也放入缓存
        localGlobalSettingMap.put(GlobalSettingConf.FILE_SETTING_PREFIX, BASE64Utils.encode(GlobalSettingConf.ALL_SETTING_CONF.toJson().getBytes()));

        // 将缓存写到本地
        LocalGlobalSettingFileUtils.writeFile(GlobalSettingConf.GLOBAL_SETTING_FILE_NAME, localGlobalSettingMap);

        return true;
    }

    /**
     * 根据当前的配置
     * 运行处理弹幕的线程
     */
    @Override
    public void startReceiveDanmuThread() {

        synchronized (GlobalSettingConf.ALL_SETTING_CONF) {

            if (ObjectUtils.isEmpty(GlobalSettingConf.ROOM_ID) || GlobalSettingConf.ROOM_ID <= 0) {
                // 这些配置都是跟直播间相关的，所以必须设置直播间
                return;
            }

            if (ObjectUtils.isEmpty(GlobalSettingConf.bilibiliWebSocketProxy) || !GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
                return;
            }

            // 到了这里说明 已经连接到了 一个直播间
            // 所以直接启动弹幕接收
            threadService.startParseMessageThread();

            // 是否使用记录日志线程
            if (GlobalSettingConf.ALL_SETTING_CONF.is_log()) {
                threadService.startLogThread();
            } else {
                threadService.closeLogThread();
            }
        }
    }



}
