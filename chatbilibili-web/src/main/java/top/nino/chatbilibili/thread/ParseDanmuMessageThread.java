package top.nino.chatbilibili.thread;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.vo.WebsocketMessagePackage;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.api.model.danmu.*;
import top.nino.api.model.superchat.SuperChat;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.rest.DanmuWebsocket;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.tool.*;
import top.nino.core.time.JodaTimeUtils;
import top.nino.core.websocket.DanmuUtils;
import top.nino.core.websocket.parse.*;
import top.nino.service.spring.SpringUtils;
import top.nino.service.chatgpt.ChatGPTService;


/**
 *
 * 解析弹幕数据的线程
 * @author nino
 */
@Slf4j
public class ParseDanmuMessageThread extends Thread {


    public volatile boolean closeFlag = false;

    private DanmuWebsocket danmuWebsocket = SpringUtils.getBean(DanmuWebsocket.class);

    private SettingService settingService = SpringUtils.getBean(SettingService.class);

    private ThreadService threadService = SpringUtils.getBean(ThreadService.class);

    private ChatGPTService chatGPTService = SpringUtils.getBean(ChatGPTService.class);


    @Override
    public void run() {

        while (!closeFlag) {

            // 当没有弹幕需要解析时，等待
            if(CollectionUtils.isEmpty(GlobalSettingCache.danmuList) || StringUtils.isBlank(GlobalSettingCache.danmuList.get(0))) {
                synchronized (GlobalSettingCache.parseDanmuMessageThread) {
                    try {
                        GlobalSettingCache.parseDanmuMessageThread.wait();
                    } catch (InterruptedException e) {
                        log.info("处理弹幕包信息线程关闭", e);
                    }
                }
            }

            // 拿取第一条消息
            String message = GlobalSettingCache.danmuList.get(0);

            JSONObject messageJsonObject = JSONObject.parseObject(message);
            log.info("收到一条未解析的消息：{}", messageJsonObject.toString());


            String cmd = DanmuUtils.parseCmd(messageJsonObject.getString("cmd"));
            if (StringUtils.isBlank(cmd)) {
                continue;
            }

            String parseResultString = "";
            String cmdResultString = "";
            Object objectResult = null;
            switch (cmd) {
                // 弹幕
                case "DANMU_MSG":

                    if(!GlobalSettingCache.ALL_SETTING_CONF.is_barrage()) {
                        break;
                    }

                    DanmuMessage danmuMessage = DanmuMessage.getDanmuMessageByJSONArray(messageJsonObject.getJSONArray("info"));

                    if(danmuMessage.getMsg_type() != 0) {
                        break;
                    }

                    // 勋章弹幕
                    boolean is_xunzhang = true;
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_anchor_shield()&& GlobalSettingCache.ROOM_ID!=null) {
                        // 房管
                        if (danmuMessage.getMedal_room() != (long) GlobalSettingCache.ROOM_ID) {
                            is_xunzhang = false;
                        }
                    }

                    if(!is_xunzhang) {
                        break;
                    }

                    DanmuUserRoleInfo danmuUserRoleInfo = DanmuUserRoleInfo.copyHbarrage(danmuMessage);
                    if (danmuMessage.getUid().equals(GlobalSettingCache.ANCHOR_UID)) {
                        danmuUserRoleInfo.setManager((short) 2);
                    }

                    StringBuilder danmuResultString = new StringBuilder();

                    // 添加弹幕时间
                    danmuResultString.append(JodaTimeUtils.formatDateTime(danmuMessage.getTimestamp()));

                    // 是不是表情弹幕
                    boolean is_emoticon = danmuMessage.getMsg_emoticon() != null && danmuMessage.getMsg_emoticon() == 1;
                    if (is_emoticon) {
                        danmuResultString.append(":收到表情:");
                    } else {
                        danmuResultString.append(":收到弹幕:");
                    }

                    // 老爷
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_vip()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseVip(danmuMessage));
                    } else {
                        danmuUserRoleInfo.setVip((short) 0);
                        danmuUserRoleInfo.setSvip((short) 0);
                    }

                    // 舰长
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_guard()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseGuard(danmuMessage));
                    } else {
                        danmuUserRoleInfo.setUguard((short) 0);
                    }

                    // 房管
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_manager()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseManager(GlobalSettingCache.ANCHOR_UID, danmuMessage));
                    } else {
                        danmuUserRoleInfo.setManager((short) 0);
                    }

                    // 勋章+勋章等级
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_medal()) {
                        if (StringUtils.isNotBlank(danmuMessage.getMedal_name())) {
                            danmuResultString.append("[").append(danmuMessage.getMedal_name()).append(" ")
                                    .append(danmuMessage.getMedal_level()).append("]");
                        }
                    } else {
                        danmuUserRoleInfo.setMedal_level(null);
                        danmuUserRoleInfo.setMedal_name(null);
                        danmuUserRoleInfo.setMedal_room(null);
                        danmuUserRoleInfo.setMedal_anchor(null);
                    }

                    // 用户等级
                    if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_ul()) {
                        danmuResultString.append(ParseDanmuUserRoleUtils.parseUserLevel(danmuMessage));
                    } else {
                        danmuUserRoleInfo.setUlevel(null);
                    }

                    danmuResultString.append(ParseDanmuUserRoleUtils.parseDanmuContent(danmuMessage));

                    parseResultString = danmuResultString.toString();
                    cmdResultString = "danmu";
                    objectResult = danmuUserRoleInfo;
                    break;

                // 送普通礼物
                case "SEND_GIFT":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                        break;
                    }

                    if(!GlobalSettingCache.ALL_SETTING_CONF.is_gift_free()) {
                        break;
                    }

                    JSONObject giftJsonObject = JSONObject.parseObject(messageJsonObject.getString("data"));
                    short gift_type = ParseIndentityTools.parseCoin_type(giftJsonObject.getString("coin_type"));

                    if(gift_type != 1) {
                        break;
                    }

                    Gift normalGift = Gift.getGiftByJsonObject(messageJsonObject, gift_type);

                    parseResultString = ParseDanmuGiftUtils.parseGiftDanmuContent(normalGift);
                    cmdResultString = "gift";
                    objectResult = normalGift;
                    break;
                // 上舰
                case "GUARD_BUY":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                        break;
                    }

                    Guard guard = JSONObject.parseObject(messageJsonObject.getString("data"), Guard.class);

                    parseResultString = ParseDanmuGuardUtils.parseGuardDanmuContent(guard);
                    cmdResultString = "gift";
                    objectResult = ParseDanmuGuardUtils.parseGuardDanmuToGiftClass(guard);
                    break;

                // 醒目留言
                case "SUPER_CHAT_MESSAGE":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                        break;
                    }

                    SuperChat superChat = JSONObject.parseObject(messageJsonObject.getString("data"), SuperChat.class);

                    parseResultString = ParseDanmuSuperChatUtils.parseSuperChatDanmeContent(superChat);
                    cmdResultString = "superchat";
                    objectResult = superChat;
                    break;

                // 醒目留言日文翻译
                case "SUPER_CHAT_MESSAGE_JPN":
                    break;

                // 删除醒目留言
                case "SUPER_CHAT_MESSAGE_DELETE":
                    break;

                // 直播间粉丝数更新 经常
                case "ROOM_REAL_TIME_MESSAGE_UPDATE":
                    GlobalSettingCache.FANS_NUM = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("fans");
                    break;

                // 直播开启
                case "LIVE":
                    GlobalSettingCache.LIVE_STATUS = 1;
                    GlobalSettingCache.IS_ROOM_POPULARITY = true;
                    break;

                // 直播超管被切断
                case "CUT_OFF":
                    break;

                // 本房间已被封禁
                case "ROOM_LOCK":
                    break;

                // 直播准备中(或者是关闭直播)
                case "PREPARING":
                    GlobalSettingCache.LIVE_STATUS = 0;
                    GlobalSettingCache.IS_ROOM_POPULARITY = false;
                    break;

                case "WATCHED_CHANGE":
                    GlobalSettingCache.ROOM_WATCHER = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("num");
                    break;

                case "POPULARITY_RED_POCKET_NEW":
                    if(!GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                        break;
                    }
                    RedPackage redPackage = JSONObject.parseObject(messageJsonObject.getString("data"), RedPackage.class);
                    String redPackageResultString = ParseDanmuRedPackageUtils.parseRedPackageDanmeContent(redPackage);

                    parseResultString = redPackageResultString;
                    cmdResultString = "gift";
                    objectResult = ParseDanmuRedPackageUtils.parseRedPackageDanmuToGiftClass(redPackage);
                    break;
                case "LIKE_INFO_V3_UPDATE":
                    GlobalSettingCache.ROOM_LIKE = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("click_count");
                    break;

                default:
                    break;
            }

            if(StringUtils.isNotBlank(parseResultString) && StringUtils.isNotBlank(cmdResultString) && ObjectUtils.isNotEmpty(objectResult)) {
                logAndSendToLocalWebSocket(parseResultString, cmdResultString, objectResult);
            }
            // 解析完成移除
            GlobalSettingCache.danmuList.remove(0);
        }

    }


    private void logAndSendToLocalWebSocket(String parseResultString, String cmd, Object result) {
        // 控制台打印
        if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
            log.info(parseResultString);
        }

        try {
            // 发送到本地网页
            danmuWebsocket.sendMessage(WebsocketMessagePackage.toJson(cmd, (short) 0, result));
        } catch (Exception e) {
            log.info("弹幕消息发送到本地网页异常", e);
        }

        // 日志处理
        if (ObjectUtils.isNotEmpty(GlobalSettingCache.logThread) && !GlobalSettingCache.logThread.FLAG) {
            GlobalSettingCache.logList.add(parseResultString);
            synchronized (GlobalSettingCache.logThread) {
                GlobalSettingCache.logThread.notify();
            }
        }
    }

}
