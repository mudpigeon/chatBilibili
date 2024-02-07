package top.nino.chatbilibili.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingConf;
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
        synchronized (GlobalSettingConf.ALL_SETTING_CONF) {

            Map<String, String> localGlobalSettingMap = new HashMap<>();

            if (ObjectUtils.isNotEmpty(GlobalSettingConf.USER)) {
                // cookie字符串放入map
                localGlobalSettingMap.put(GlobalSettingConf.FILE_COOKIE_PREFIX, BASE64Utils.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }

            // 页面上的所有配置
            localGlobalSettingMap.put(GlobalSettingConf.FILE_SETTING_PREFIX, BASE64Utils.encode(GlobalSettingConf.ALL_SETTING_CONF.toJson().getBytes()));

            // 将当前的全局配置写到本地
            LocalGlobalSettingFileUtils.writeFile(GlobalSettingConf.GLOBAL_SETTING_FILE_NAME, localGlobalSettingMap);

            try {
                // 再读出来
                GlobalSettingConf.ALL_SETTING_CONF = JSONObject.parseObject(
                        new String(BASE64Utils.decode(LocalGlobalSettingFileUtils.readFile(GlobalSettingConf.GLOBAL_SETTING_FILE_NAME).get(GlobalSettingConf.FILE_SETTING_PREFIX))),
                        AllSettingConfig.class);
            } catch (Exception e) {
                log.error("读取配置文件历史房间失败:" + e);
            }

            if (ObjectUtils.isNotEmpty(GlobalSettingConf.ROOM_ID)) {
                globalSettingFileService.startReceiveDanmuThread();
            }
            localGlobalSettingMap.clear();
        }
    }

    @Override
    public void clearLoginCache() {
        GlobalSettingConf.clearUserCache();
        threadService.closeUser();
        log.info("用户退出成功");
    }

}
