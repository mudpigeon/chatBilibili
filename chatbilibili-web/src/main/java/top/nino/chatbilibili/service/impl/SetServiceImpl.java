package top.nino.chatbilibili.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.service.componect.ServerAddressComponent;
import top.nino.chatbilibili.component.ThreadComponent;
import top.nino.chatbilibili.conf.base.CenterSetConf;
import top.nino.chatbilibili.http.HttpOtherData;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SetService;
import top.nino.chatbilibili.tool.CurrencyTools;
import top.nino.chatbilibili.tool.ParseSetStatusTools;
import top.nino.core.*;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author nino
 */
@Slf4j
@Service
public class SetServiceImpl implements SetService {

    private final String cookies = "ySZL4SBB";

    @Autowired
    private ClientService clientService;
    @Autowired
    private ThreadComponent threadComponent;
    @Autowired
    private ServerAddressComponent serverAddressComponent;



    public void changeSet(CenterSetConf centerSetConf) {
        synchronized (centerSetConf) {
            Map<String, String> profileMap = new ConcurrentHashMap<>();
            BASE64Encoder base64Encoder = new BASE64Encoder();
            if (GlobalSettingConf.USER != null) {
                profileMap.put(cookies, base64Encoder.encode(GlobalSettingConf.COOKIE_VALUE.getBytes()));
            }
            profileMap.put("set", base64Encoder.encode(centerSetConf.toJson().getBytes()));
            ProFileTools.write(profileMap, "DanmujiProfile");
            log.info("保存配置文件成功");
            base64Encoder = null;
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
            ProFileTools.write(profileMap, "DanmujiProfile");
            try {
                GlobalSettingConf.centerSetConf = JSONObject.parseObject(
                        new String(base64Encoder.decode(ProFileTools.read("DanmujiProfile").get("set"))),
                        CenterSetConf.class);
                holdSet(centerSetConf);
                log.info("保存配置文件成功");
            } catch (Exception e) {
                // TODO: handle exception
                log.error("保存配置文件失败:" + e);
            }
            base64Encoder = null;
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
            ProFileTools.write(profileMap, "DanmujiProfile");
            try {
                GlobalSettingConf.centerSetConf = JSONObject.parseObject(
                        new String(base64Encoder.decode(ProFileTools.read("DanmujiProfile").get("set"))),
                        CenterSetConf.class);
                GlobalSettingConf.centerSetConf = ParseSetStatusTools.initCenterChildConfig(GlobalSettingConf.centerSetConf);
                if (GlobalSettingConf.ROOMID != null) {
                    holdSet(centerSetConf);
                }
                log.info("读取配置文件历史房间成功");
            } catch (Exception e) {
                // TODO: handle exception
                log.error("读取配置文件历史房间失败:" + e);
            }
            base64Encoder = null;
            profileMap.clear();
        }
    }


    /**
     * 保存配置并执行
     */
    public void holdSet(CenterSetConf centerSetConf) {

        synchronized (centerSetConf) {

            SchedulingRunnableUtil task = new SchedulingRunnableUtil("dosignTask", "dosign");
            SchedulingRunnableUtil dakatask = new SchedulingRunnableUtil("dosignTask", "clockin");
            SchedulingRunnableUtil autoSendGiftTask = new SchedulingRunnableUtil("dosignTask", "autosendgift");

            // 每日签到
            if (GlobalSettingConf.centerSetConf.is_dosign()) {
                // 判断签到
                boolean isSign = CurrencyTools.signNow();
                if (isSign) {
                    changeSet(GlobalSettingConf.centerSetConf);
                }
//                if (!taskRegisterComponent.hasTask(task)) {
//                    taskRegisterComponent.addTask(task, CurrencyTools.dateStringToCron(centerSetConf.getSign_time()));
//                }
            } else {
                try {
//                    taskRegisterComponent.removeTask(task);
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                    log.error("清理定时任务错误：" + e);
                }
            }

            // 每日打卡
            if (centerSetConf.getClock_in().is_open()) {
//                if (!taskRegisterComponent.hasTask(dakatask)) {
//                    taskRegisterComponent.addTask(dakatask, CurrencyTools.dateStringToCron(centerSetConf.getClock_in().getTime()));
//                }
            } else {
                try {
//                    taskRegisterComponent.removeTask(dakatask);
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                    log.error("清理定时任务错误：" + e);
                }
            }

            // 每日定时自动送礼
            if (centerSetConf.getAuto_gift().is_open()) {
//                if (!taskRegisterComponent.hasTask(autoSendGiftTask)) {
//                    taskRegisterComponent.addTask(autoSendGiftTask, CurrencyTools.dateStringToCron(centerSetConf.getAuto_gift().getTime()));
//                }
            } else {
                try {
//                    taskRegisterComponent.removeTask(autoSendGiftTask);
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                    log.error("清理定时任务错误：" + e);
                }
            }

            // need roomid set
            if (GlobalSettingConf.ROOMID == null || GlobalSettingConf.ROOMID <= 0) {
                return;
            }

            if (GlobalSettingConf.webSocketProxy == null) {
                return;
            }

            if (GlobalSettingConf.webSocketProxy != null && !GlobalSettingConf.webSocketProxy.isOpen()) return;

            // parsemessagethread start
            threadComponent.startParseMessageThread(centerSetConf);

            // logthread
            if (centerSetConf.is_log()) {
                threadComponent.startLogThread();
            } else {
                threadComponent.closeLogThread();
            }

            // need login
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                // advertthread
                centerSetConf.getAdvert().start(threadComponent);
                // autoreplythread
                centerSetConf.getReply().start(threadComponent);
                // useronlinethread && smallHeartThread 移除在线心跳 接口已经不可用
//                if (centerSetConf.is_online()) {
//                    threadComponent.startUserOnlineThread();
//                    if (centerSetConf.is_sh() && PublicDataConf.lIVE_STATUS == 1) {
//                        threadComponent.startSmallHeartThread();
//                    } else {
//                        threadComponent.closeSmallHeartThread();
//                    }
//                } else {
//                    threadComponent.closeSmallHeartThread();
//                    threadComponent.closeUserOnlineThread();
//                }
                // sendbarragethread
                if (GlobalSettingConf.advertThread == null
                        && !GlobalSettingConf.centerSetConf.getFollow().is_followThank()
                        && !GlobalSettingConf.centerSetConf.getWelcome().is_welcomeThank()
                        && !GlobalSettingConf.centerSetConf.getThank_gift().is_giftThank()
                        && GlobalSettingConf.autoReplyThread == null) {
                    threadComponent.closeSendBarrageThread();
                    GlobalSettingConf.init_send();
                } else {
                    threadComponent.startSendBarrageThread();
                }
            } else {
                //没有登录
                GlobalSettingConf.init_user();
                threadComponent.closeUser(false);
            }
            if (GlobalSettingConf.webSocketProxy != null && !GlobalSettingConf.webSocketProxy.isOpen()) {
                threadComponent.closeAll();
                GlobalSettingConf.init_all();
            }
        }
    }

    public void quit() {
        GlobalSettingConf.init_user();
        threadComponent.closeUser(true);
        // remove task all shutdown !!!!!!
        try {
//            taskRegisterComponent.destroy();
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            log.error("清理定时任务错误：" + e);
        }
        GlobalSettingConf.init_send();
        holdSet(GlobalSettingConf.centerSetConf);
        log.info("用户退出成功");
    }




}
