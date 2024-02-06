package top.nino.chatbilibili.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.service.componect.ServerAddressComponent;
import top.nino.chatbilibili.component.ThreadComponent;
import top.nino.chatbilibili.conf.base.AllSettingConfig;
import top.nino.chatbilibili.service.ClientService;
import top.nino.core.*;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author nino
 */
@Slf4j
@Service
public class SettingServiceImpl implements SettingService {


    @Autowired
    private ClientService clientService;
    @Autowired
    private ThreadComponent threadComponent;
    @Autowired
    private ServerAddressComponent serverAddressComponent;

    @Autowired
    private GlobalSettingFileService globalSettingFileService;



    public void changeSet(AllSettingConfig allSettingConfig) {
        synchronized (allSettingConfig) {
            Map<String, String> profileMap = new ConcurrentHashMap<>();
            BASE64Encoder base64Encoder = new BASE64Encoder();
            if (GlobalSettingConf.USER != null) {
                profileMap.put(GlobalSettingConf.FILE_COOKIE_PREFIX, base64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
            profileMap.put("set", base64Encoder.encode(allSettingConfig.toJson().getBytes()));
            LocalGlobalSettingFileUtil.writeFile("DanmujiProfile", profileMap);
            log.info("保存配置文件成功");
            profileMap.clear();
        }
    }

    // 保存配置文件
    public void changeSet(AllSettingConfig allSettingConfig, boolean check) {
        synchronized (allSettingConfig) {
            if (allSettingConfig.toJson().equals(GlobalSettingConf.ALL_SETTING_CONF.toJson())&&check) {
                log.info("保存配置文件成功");
                return;
            }
            if (GlobalSettingConf.ROOMID_LONG != null && GlobalSettingConf.ROOMID_LONG > 0) {
                allSettingConfig.setRoomId(GlobalSettingConf.ROOMID_LONG);
            }
            Map<String, String> profileMap = new ConcurrentHashMap<>();
            BASE64Encoder base64Encoder = new BASE64Encoder();
            if (GlobalSettingConf.USER != null) {
                profileMap.put(GlobalSettingConf.FILE_COOKIE_PREFIX, base64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
            profileMap.put("set", base64Encoder.encode(allSettingConfig.toJson().getBytes()));
            LocalGlobalSettingFileUtil.writeFile("DanmujiProfile", profileMap);
            try {
                GlobalSettingConf.ALL_SETTING_CONF = JSONObject.parseObject(
                        new String(base64Encoder.decode(LocalGlobalSettingFileUtil.readFile("DanmujiProfile").get("set"))),
                        AllSettingConfig.class);
                globalSettingFileService.startReceiveDanmuThread();
                log.info("保存配置文件成功");
            } catch (Exception e) {
                // TODO: handle exception
                log.error("保存配置文件失败:" + e);
            }
            profileMap.clear();
        }
    }

    @Override
    public void connectSet() {
        synchronized (GlobalSettingConf.ALL_SETTING_CONF) {

            Map<String, String> localGlobalSettingMap = new HashMap<>();

            // 将当前的全局配置写到本地
            if (ObjectUtils.isNotEmpty(GlobalSettingConf.USER)) {
                // cookie字符串放入map
                localGlobalSettingMap.put(GlobalSettingConf.FILE_COOKIE_PREFIX, BASE64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
            localGlobalSettingMap.put(GlobalSettingConf.FILE_SETTING_PREFIX, BASE64Encoder.encode(GlobalSettingConf.ALL_SETTING_CONF.toJson().getBytes()));
            LocalGlobalSettingFileUtil.writeFile(GlobalSettingConf.GLOBAL_SETTING_FILE_NAME, localGlobalSettingMap);

            try {
                // 再读出来
                GlobalSettingConf.ALL_SETTING_CONF = JSONObject.parseObject(
                        new String(BASE64Encoder.decode(LocalGlobalSettingFileUtil.readFile(GlobalSettingConf.GLOBAL_SETTING_FILE_NAME).get(GlobalSettingConf.FILE_SETTING_PREFIX))),
                        AllSettingConfig.class);

                if (ObjectUtils.isNotEmpty(GlobalSettingConf.ROOM_ID)) {
                    globalSettingFileService.startReceiveDanmuThread();
                }
                log.info("读取配置文件历史房间成功");
            } catch (Exception e) {
                log.error("读取配置文件历史房间失败:" + e);
            }
            localGlobalSettingMap.clear();
        }
    }

    @Override
    public void clearLoginCache() {
        GlobalSettingConf.clearUserCache();
        threadComponent.closeUser(true);
        log.info("用户退出成功");
    }

}
