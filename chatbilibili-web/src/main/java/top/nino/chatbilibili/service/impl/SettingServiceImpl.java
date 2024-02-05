package top.nino.chatbilibili.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.service.componect.ServerAddressComponent;
import top.nino.chatbilibili.component.ThreadComponent;
import top.nino.chatbilibili.conf.base.CenterSetConf;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.tool.ParseSetStatusTools;
import top.nino.core.*;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author nino
 */
@Slf4j
@Service
public class SettingServiceImpl implements SettingService {

    private final String cookies = "ySZL4SBB";

    @Autowired
    private ClientService clientService;
    @Autowired
    private ThreadComponent threadComponent;
    @Autowired
    private ServerAddressComponent serverAddressComponent;

    @Autowired
    private GlobalSettingFileService globalSettingFileService;



    public void changeSet(CenterSetConf centerSetConf) {
        synchronized (centerSetConf) {
            Map<String, String> profileMap = new ConcurrentHashMap<>();
            BASE64Encoder base64Encoder = new BASE64Encoder();
            if (GlobalSettingConf.USER != null) {
                profileMap.put(cookies, base64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
            profileMap.put("set", base64Encoder.encode(centerSetConf.toJson().getBytes()));
            LocalGlobalSettingFileUtil.writeFile(profileMap, "DanmujiProfile");
            log.info("保存配置文件成功");
            profileMap.clear();
        }
    }

    // 保存配置文件
    public void changeSet(CenterSetConf centerSetConf,boolean check) {
        synchronized (centerSetConf) {
            if (centerSetConf.toJson().equals(GlobalSettingConf.centerSetConf.toJson())&&check) {
                log.info("保存配置文件成功");
                return;
            }
            if (GlobalSettingConf.ROOMID_LONG != null && GlobalSettingConf.ROOMID_LONG > 0) {
                centerSetConf.setRoomid(GlobalSettingConf.ROOMID_LONG);
            }
            Map<String, String> profileMap = new ConcurrentHashMap<>();
            BASE64Encoder base64Encoder = new BASE64Encoder();
            if (GlobalSettingConf.USER != null) {
                profileMap.put(cookies, base64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
            profileMap.put("set", base64Encoder.encode(centerSetConf.toJson().getBytes()));
            LocalGlobalSettingFileUtil.writeFile(profileMap, "DanmujiProfile");
            try {
                GlobalSettingConf.centerSetConf = JSONObject.parseObject(
                        new String(base64Encoder.decode(LocalGlobalSettingFileUtil.readFile("DanmujiProfile").get("set"))),
                        CenterSetConf.class);
                globalSettingFileService.reConnectRoom();
                log.info("保存配置文件成功");
            } catch (Exception e) {
                // TODO: handle exception
                log.error("保存配置文件失败:" + e);
            }
            profileMap.clear();
        }
    }

    public void connectSet(CenterSetConf centerSetConf) {
        synchronized (centerSetConf) {
            Map<String, String> profileMap = new ConcurrentHashMap<>();
            BASE64Encoder base64Encoder = new BASE64Encoder();
            if (GlobalSettingConf.USER != null) {
                profileMap.put(cookies, base64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
            profileMap.put("set", base64Encoder.encode(centerSetConf.toJson().getBytes()));
            LocalGlobalSettingFileUtil.writeFile(profileMap, "DanmujiProfile");
            try {
                GlobalSettingConf.centerSetConf = JSONObject.parseObject(
                        new String(base64Encoder.decode(LocalGlobalSettingFileUtil.readFile("DanmujiProfile").get("set"))),
                        CenterSetConf.class);
                GlobalSettingConf.centerSetConf = ParseSetStatusTools.initCenterChildConfig(GlobalSettingConf.centerSetConf);
                if (GlobalSettingConf.ROOMID != null) {
                    globalSettingFileService.reConnectRoom();
                }
                log.info("读取配置文件历史房间成功");
            } catch (Exception e) {
                // TODO: handle exception
                log.error("读取配置文件历史房间失败:" + e);
            }
            profileMap.clear();
        }
    }

    @Override
    public void clearLoginCache() {
        GlobalSettingConf.clearUserCache();
        threadComponent.closeUser(true);
        log.info("用户退出成功");
    }




}
