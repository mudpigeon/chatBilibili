package top.nino.chatbilibili.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.AllSettingConfig;
import top.nino.chatbilibili.service.ClientService;
import top.nino.core.data.BASE64Utils;
import top.nino.core.file.LocalGlobalSettingFileUtils;


import java.util.HashMap;
import java.util.Map;


/**
 * @author nino
 */
@Slf4j
@Service
public class SettingServiceImpl implements SettingService {


    @Autowired
    private ClientService clientService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private GlobalSettingFileService globalSettingFileService;





    @Override
    public void writeAndReadSettingAndStartReceive() {
        synchronized (GlobalSettingCache.ALL_SETTING_CONF) {

            Map<String, String> localGlobalSettingMap = new HashMap<>();

            if (ObjectUtils.isNotEmpty(GlobalSettingCache.USER)) {
                // cookie字符串放入map
                localGlobalSettingMap.put(GlobalSettingCache.FILE_COOKIE_PREFIX, BASE64Utils.encode(GlobalSettingCache.COOKIE_VALUE.getBytes()));
            }

            // 页面上的所有配置
            localGlobalSettingMap.put(GlobalSettingCache.FILE_SETTING_PREFIX, BASE64Utils.encode(GlobalSettingCache.ALL_SETTING_CONF.toJson().getBytes()));

            // 将当前的全局配置写到本地
            LocalGlobalSettingFileUtils.writeFile(GlobalSettingCache.GLOBAL_SETTING_FILE_NAME, localGlobalSettingMap);

            try {
                // 再读出来
                GlobalSettingCache.ALL_SETTING_CONF = JSONObject.parseObject(
                        new String(BASE64Utils.decode(LocalGlobalSettingFileUtils.readFile(GlobalSettingCache.GLOBAL_SETTING_FILE_NAME).get(GlobalSettingCache.FILE_SETTING_PREFIX))),
                        AllSettingConfig.class);
            } catch (Exception e) {
                log.error("读取配置文件历史房间失败:" + e);
            }

            if (ObjectUtils.isNotEmpty(GlobalSettingCache.ROOM_ID)) {
                globalSettingFileService.startReceiveDanmuThread();
            }
            localGlobalSettingMap.clear();
        }
    }

    @Override
    public void clearLoginCache() {
        GlobalSettingCache.clearUserCache();
        threadService.closeUser();
        log.info("用户退出成功");
    }

}
