package top.nino.chatbilibili.thread;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.superchat.MedalInfo;
import top.nino.api.model.vo.WebsocketMessagePackage;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.api.model.danmu.*;
import top.nino.api.model.superchat.SuperChat;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.rest.DanmuWebsocket;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.tool.*;
import top.nino.core.time.JodaTimeUtils;
import top.nino.core.websocket.ParseDanmuUserRoleUtils;
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
        try {
            Gift gift = null;
            Interact interact = null;
            Guard guard = null;
            SuperChat superChat = null;

            RedPackage redPackage = null;
            short msg_type = 0;
            StringBuilder stringBuilder = new StringBuilder(200);

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

                String message = GlobalSettingCache.danmuList.get(0);
                JSONObject messageJsonObject = JSONObject.parseObject(message);


                String cmd = parseCmd(messageJsonObject.getString("cmd"));
                if (StringUtils.isBlank(cmd)) {
                    continue;
                }



                switch (cmd) {
                    // 弹幕
                    case "DANMU_MSG":

                        if(!GlobalSettingCache.ALL_SETTING_CONF.is_barrage()) {
                            break;
                        }

                        DanmuMessage danmuMessage = DanmuMessage.getDanmuMessageByJSONArray(messageJsonObject.getJSONArray("info"));

                        // 勋章弹幕
                        boolean is_xunzhang = true;
                        if (GlobalSettingCache.ALL_SETTING_CONF.is_barrage_anchor_shield()&& GlobalSettingCache.ROOM_ID!=null) {
                            // 房管
                            if (danmuMessage.getMedal_room() != (long) GlobalSettingCache.ROOM_ID) {
                                is_xunzhang = false;
                            }
                        }


                        // 普通弹幕
                        if (danmuMessage.getMsg_type() == 0 && is_xunzhang) {
                            // 高级弹幕
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

                            // 控制台打印
                            if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
                                log.info(danmuResultString.toString());
                            }

                            try {
                                // 发送到本地网页
                                danmuWebsocket.sendMessage(WebsocketMessagePackage.toJson("danmu", (short) 0, danmuUserRoleInfo));
                            } catch (Exception e) {
                                log.info("弹幕消息发送到本地网页异常", e);
                            }

                            // 日志处理
                            if (ObjectUtils.isNotEmpty(GlobalSettingCache.logThread) && !GlobalSettingCache.logThread.FLAG) {
                                GlobalSettingCache.logList.add(stringBuilder.toString());
                                synchronized (GlobalSettingCache.logThread) {
                                    GlobalSettingCache.logThread.notify();
                                }
                            }

                            stringBuilder.delete(0, stringBuilder.length());
                        }
                        break;

                    // 送普通礼物
                    case "SEND_GIFT":
                        messageJsonObject = JSONObject.parseObject(messageJsonObject.getString("data"));
                        short gift_type = ParseIndentityTools.parseCoin_type(messageJsonObject.getString("coin_type"));
                        gift = Gift.getGift(messageJsonObject.getInteger("giftId"), messageJsonObject.getShort("giftType"),
                                messageJsonObject.getString("giftName"), messageJsonObject.getInteger("num"),
                                messageJsonObject.getString("uname"), messageJsonObject.getString("face"),
                                messageJsonObject.getShort("guard_level"), messageJsonObject.getLong("uid"),
                                messageJsonObject.getLong("timestamp"), messageJsonObject.getString("action"),
                                messageJsonObject.getInteger("price"),
                                gift_type,
                                messageJsonObject.getLong("total_coin"), messageJsonObject.getObject("medal_info", MedalInfo.class));
                        if (GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                            if (GlobalSettingCache.ALL_SETTING_CONF.is_gift_free() || (!GlobalSettingCache.ALL_SETTING_CONF.is_gift_free() && gift_type == 1)) {
                                stringBuilder.append(JodaTimeUtils.formatDateTime(gift.getTimestamp() * 1000));
                                stringBuilder.append(":收到道具:");
                                stringBuilder.append(gift.getUname());
                                stringBuilder.append(" ");
                                stringBuilder.append(gift.getAction());
                                stringBuilder.append("的:");
                                stringBuilder.append(gift.getGiftName());
                                stringBuilder.append(" x ");
                                stringBuilder.append(gift.getNum());
                                //控制台打印
                                if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
                                    System.out.println(stringBuilder.toString());
                                }
                                try {
                                    danmuWebsocket.sendMessage(WebsocketMessagePackage.toJson("gift", (short) 0, gift));
                                } catch (Exception e) {
                                    // TODO 自动生成的 catch 块
                                    e.printStackTrace();
                                }
                                if (GlobalSettingCache.logThread != null && !GlobalSettingCache.logThread.FLAG) {
                                    GlobalSettingCache.logList.add(stringBuilder.toString());
                                    synchronized (GlobalSettingCache.logThread) {
                                        GlobalSettingCache.logThread.notify();
                                    }
                                }
                                stringBuilder.delete(0, stringBuilder.length());
                            }
                        } else {
                            //礼物关闭
                        }

                        break;

                    // 部分金瓜子礼物连击
                    case "COMBO_SEND":
                        //					LOGGER.info("部分金瓜子礼物连击:::" + message);
                        break;

                    // 部分金瓜子礼物连击
                    case "COMBO_END":
                        //					LOGGER.info("部分金瓜子礼物连击:::" + message);
                        break;

                    // 上舰
                    case "GUARD_BUY":
                        if (GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                            guard = JSONObject.parseObject(messageJsonObject.getString("data"), Guard.class);
                            stringBuilder.append(JodaTimeUtils.formatDateTime(guard.getStart_time() * 1000));
                            stringBuilder.append(":有人上船:");
                            stringBuilder.append(guard.getUsername());
                            stringBuilder.append("在本房间开通了");
                            stringBuilder.append(guard.getNum());
                            stringBuilder.append("个月");
                            stringBuilder.append(guard.getGift_name());
                            //控制台打印
                            if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
                                System.out.println(stringBuilder.toString());
                            }
                            gift = new Gift();
                            gift.setGiftName(guard.getGift_name());
                            gift.setNum(guard.getNum());
                            gift.setPrice(guard.getPrice());
                            gift.setTotal_coin((long) guard.getNum() * guard.getPrice());
                            gift.setTimestamp(guard.getStart_time());
                            gift.setAction("赠送");
                            gift.setCoin_type((short) 1);
                            gift.setUname(guard.getUsername());
                            gift.setUid(guard.getUid());
                            try {
                                danmuWebsocket.sendMessage(WebsocketMessagePackage.toJson("gift", (short) 0, gift));
                            } catch (Exception e) {
                                // TODO 自动生成的 catch 块
                                e.printStackTrace();
                            }
                            if (GlobalSettingCache.logThread != null && !GlobalSettingCache.logThread.FLAG) {
                                GlobalSettingCache.logList.add(stringBuilder.toString());
                                synchronized (GlobalSettingCache.logThread) {
                                    GlobalSettingCache.logThread.notify();
                                }
                            }
                            stringBuilder.delete(0, stringBuilder.length());
                        }
                        break;

                    // 上舰消息推送
                    case "GUARD_LOTTERY_START":
                        //					LOGGER.info("上舰消息推送:::" + message);
                        break;

                    // 上舰抽奖消息推送
                    case "USER_TOAST_MSG":
                        //					LOGGER.info("上舰抽奖消息推送:::" + message);
                        break;

                    // 醒目留言
                    case "SUPER_CHAT_MESSAGE":
                        if (GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                            superChat = JSONObject.parseObject(messageJsonObject.getString("data"), SuperChat.class);
                            stringBuilder.append(JodaTimeUtils.formatDateTime(superChat.getStart_time() * 1000));
                            stringBuilder.append(":收到留言:");
                            stringBuilder.append(superChat.getUser_info().getUname());
                            stringBuilder.append(" 他用了");
                            //适配6.11破站更新金瓜子为电池  叔叔真有你的
                            stringBuilder.append(superChat.getPrice() * 10);
                            stringBuilder.append("电池留言了");
                            stringBuilder.append(ParseIndentityTools.parseTime(superChat.getTime()));
                            stringBuilder.append("秒说: ");
                            stringBuilder.append(superChat.getMessage());
                            superChat.setTime(ParseIndentityTools.parseTime(superChat.getTime()));
                            //控制台打印
                            if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
                                System.out.println(stringBuilder.toString());
                            }
                            try {
                                danmuWebsocket.sendMessage(WebsocketMessagePackage.toJson("superchat", (short) 0, superChat));
                            } catch (Exception e) {
                                // TODO 自动生成的 catch 块
                                e.printStackTrace();
                            }
                            if (GlobalSettingCache.logThread != null && !GlobalSettingCache.logThread.FLAG) {
                                GlobalSettingCache.logList.add(stringBuilder.toString());
                                synchronized (GlobalSettingCache.logThread) {
                                    GlobalSettingCache.logThread.notify();
                                }
                            }

                            stringBuilder.delete(0, stringBuilder.length());
                        }

                        break;

                    // 醒目留言日文翻译
                    case "SUPER_CHAT_MESSAGE_JPN":
                        //					LOGGER.info("醒目留言日文翻译消息推送:::" + message);
                        break;

                    // 删除醒目留言
                    case "SUPER_CHAT_MESSAGE_DELETE":
                        //					LOGGER.info("该条醒目留言已被删除:::" + message);
                        break;

                    // 欢迎老爷进来本直播间
                    case "WELCOME":
                        // 区分年月费老爷
                        /*
                         * if(welcomVip.getSvip()==1) {
                         * System.out.println(JodaTimeUtils.getCurrentTimeString()+":欢迎年费老爷:"+welcomeVip
                         * .getUname()+" 进入直播间"); }else {
                         * System.out.println(JodaTimeUtils.getCurrentTimeString()+":欢迎月费老爷:"+welcomeVip
                         * .getUname()+" 进入直播间"); }
                         */

                        break;

                    // 欢迎舰长进入直播间
                    case "WELCOME_GUARD":
                        break;

                    // 舰长进入直播间消息
                    case "ENTRY_EFFECT":
                        //					LOGGER.info("舰长大大进入直播间消息推送:::" + message);
                        break;

                    // 节奏风暴推送 action 为start和end
                    case "SPECIAL_GIFT":
                        //					LOGGER.info("节奏风暴推送:::" + message);
                        break;

                    // 禁言消息
                    case "ROOM_BLOCK_MSG":

                        break;

                    // 本主播在本分区小时榜排名更新推送 不会更新页面的排行显示信息
                    case "ACTIVITY_BANNER_UPDATE_V2":
                        //					LOGGER.info("小时榜消息更新推送:::" + message);
                        break;

                    // 本房间分区修改
                    case "ROOM_CHANGE":
                        //					LOGGER.info("房间分区已更新:::" + message);
                        break;

                    // 本房间分区排行榜更新 更新页面的排行显示信息
                    case "ROOM_RANK":
                        //					rannk = JSONObject.parseObject(jsonObject.getString("data"), Rannk.class);
                        //					stringBuilder.append(JodaTimeUtils.format(rannk.getTimestamp() * 1000)).append(":榜单更新:")
                        //							.append(rannk.getRank_desc());
                        //
                        //					System.out.println(stringBuilder.toString());
                        //					stringBuilder.delete(0, stringBuilder.length());
                        //					LOGGER.info("小时榜信息更新推送:::" + message);
                        break;

                    // 推测为获取本小时榜榜单第一名主播的信息 推测激活条件为本直播间获得第一
                    case "new_anchor_reward":
                        //					LOGGER.info("获取本小时榜榜单第一名主播的信息:::" + message);
                        break;

                    // 小时榜榜单信息推送 推测激活条件为本直播间获得第一
                    case "HOUR_RANK_AWARDS":
                        //					LOGGER.info("恭喜xxxx直播间获得:::" + message);
                        break;

                    // 直播间粉丝数更新 经常
                    case "ROOM_REAL_TIME_MESSAGE_UPDATE":
                        //					fans = JSONObject.parseObject(jsonObject.getString("data"), Fans.class);
                        //					stringBuilder.append(JodaTimeUtils.getCurrentTimeString()).append(":消息推送:").append("房间号")
                        //							.append(fans.getRoomid()).append("的粉丝数:").append(fans.getFans());
                        GlobalSettingCache.FANS_NUM = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("fans");
                        //					System.out.println(stringBuilder.toString());
                        //					stringBuilder.delete(0, stringBuilder.length());
                        //					LOGGER.info("直播间粉丝数更新消息推送:::" + message);
                        break;

                    // 直播间许愿瓶消息推送更新
                    case "WISH_BOTTLE":
                        //					LOGGER.info("直播间许愿瓶消息推送更新:::" + message);
                        break;

                    // 广播小电视类抽奖信息推送,包括本房间的舰长礼物包括,本直播间所在小时榜榜单主播信息的推送 需要unicode转义 免费辣条再见！！！！
                    case "NOTICE_MSG":
                        //					message = ByteUtils.unicodeToString(message);
                        //					LOGGER.info("小电视类抽奖信息推送:::" + message);
                        break;

                    // 本房间开启活动抽奖(33图,小电视图,任意门等) 也指本房间内赠送的小电视 摩天大楼类抽奖
                    case "RAFFLE_START":
                        //					LOGGER.info("本房间开启了活动抽奖:::" + message);
                        break;

                    // 本房间活动中奖用户信息推送 也指抽奖结束
                    case "RAFFLE_END":
                        //					LOGGER.info("看看谁是幸运儿:::" + message);
                        break;

                    // 本房间主播开启了天选时刻
                    case "ANCHOR_LOT_START":
//                            LOGGER.info("本房间主播开启了天选时刻:::" + message);
                        if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {

                        }
                        //					LOGGER.info("本房间主播开启了天选时刻:::" + message);
                        break;

                    // 本房间天选时刻结束
                    case "ANCHOR_LOT_END":
                        //					LOGGER.info("本房间天选时刻结束:::" + message);
                        break;

                    // 本房间天选时刻获奖信息推送
                    case "ANCHOR_LOT_AWARD":
                        //					LOGGER.info("本房间天选时刻中奖用户是:::" + message);
                        break;

                    // 获得推荐位推荐消息
                    case "ANCHOR_NORMAL_NOTIFY":
                        //					LOGGER.info("本房间获得推荐位:::" + message);
                        break;
                    // 周星消息推送
                    case "WEEK_STAR_CLOCK":
                        //			        LOGGER.info("周星消息推送:::" + message);
                        break;

                    // 推测本主播周星信息更新
                    case "ROOM_BOX_MASTER":
                        //					LOGGER.info("周星信息更新:::" + message);
                        break;

                    // 周星消息推送关闭
                    case "ROOM_SKIN_MSG":
                        //					LOGGER.info("周星消息推送关闭:::" + message);
                        break;

                    // 中型礼物多数量赠送消息推送 例如b克拉 亿元
                    case "SYS_GIFT":
                        //					LOGGER.info("中型礼物多数量赠送消息推送:::" + message);
                        break;

                    // lol活动礼物？？？
                    case "ACTIVITY_MATCH_GIFT":
                        //					LOGGER.info("lol专属房间礼物赠送消息推送:::" + message);
                        break;

                    //----------------------------------------pk信息多为要uicode解码-------------------------------------------------
                    // 推测为房间pk信息推送
                    case "PK_BATTLE_ENTRANCE":
                        //			        LOGGER.info("房间pk活动信息推送:::" + message);
                        break;

                    // 活动pk准备
                    case "PK_BATTLE_PRE":
                        //			        LOGGER.info("房间活动pk准备:::" + message);
                        break;

                    // 活动pk开始
                    case "PK_BATTLE_START":
                        //			        LOGGER.info("房间活动pk开始:::" + message);
                        break;

                    // 活动pk中
                    case "PK_BATTLE_PROCESS":
                        //			        LOGGER.info("房间活动pk中:::" + message);
                        break;

                    // 活动pk详细信息
                    case "PK_BATTLE_CRIT":
                        //			        LOGGER.info("房间活动pk详细信息推送:::" + message);
                        break;

                    // 活动pk类型推送
                    case "PK_BATTLE_PRO_TYPE":
                        //			        LOGGER.info("房间活动pk类型推送:::" + message);
                        break;

                    // 房间活动pk结束
                    case "PK_BATTLE_END":
                        //			        LOGGER.info("房间pk活动结束::" + message);
                        break;

                    // 活动pk结果用户 推送
                    case "PK_BATTLE_SETTLE_USER":
                        //			        LOGGER.info("活动pk结果用户 推送::" + message);
                        break;

                    // 活动pk礼物开始 1辣条
                    case "PK_LOTTERY_START":
                        //			        LOGGER.info("活动pk礼物开始 推送::" + message);
                        break;

                    // 活动pk结果房间
                    case "PK_BATTLE_SETTLE":
                        //			        LOGGER.info("活动pk结果房间推送::" + message);
                        break;

                    // pk开始
                    case "PK_START":
                        //					LOGGER.info("房间pk开始:::" + message);
                        break;

                    // pk准备中
                    case "PK_PRE":
                        //					LOGGER.info("房间pk准备中:::" + message);
                        break;

                    // pk载入中
                    case "PK_MATCH":
                        //					LOGGER.info("房间pk载入中:::" + message);
                        break;

                    // pk再来一次触发
                    case "PK_CLICK_AGAIN":
                        //					LOGGER.info("房间pk再来一次:::" + message);
                        break;
                    // pk结束
                    case "PK_MIC_END":
                        //					LOGGER.info("房间pk结束:::" + message);
                        break;

                    // pk礼物信息推送 激活条件推测为pk胜利 可获得一个辣条
                    case "PK_PROCESS":
                        //					LOGGER.info("房间pk礼物推送:::" + message);
                        break;

                    // pk结果信息推送
                    case "PK_SETTLE":
                        //					LOGGER.info("房间pk结果信息推送:::" + message);
                        break;

                    // pk结束信息推送
                    case "PK_END":
                        //					LOGGER.info("房间pk结束信息推送:::" + message);
                        break;

                    // 系统信息推送
                    case "SYS_MSG":
                        //					LOGGER.info("系统信息推送:::" + message);
                        break;

                    // 总督登场消息
                    case "GUARD_MSG":
                        //					LOGGER.info("总督帅气登场:::" + message);
                        break;

                    // 热门房间？？？？广告房间？？ 不知道这是什么 推测本直播间激活 目前常见于打广告的官方直播间 例如手游 碧蓝航线 啥的。。
                    case "HOT_ROOM_NOTIFY":
                        //					LOGGER.info("热门房间推送消息:::" + message);
                        break;

                    // 小时榜面板消息推送
                    case "PANEL":
                        //					LOGGER.info("热小时榜面板消息推送:::" + message);
                        break;

                    // 星之耀宝箱使用 n
                    case "ROOM_BOX_USER":
                        //					LOGGER.info("星之耀宝箱使用:::" + message);
                        break;

                    // 语音加入？？？？ 暂不知道
                    case "VOICE_JOIN_ROOM_COUNT_INFO":
                        //					LOGGER.info("语音加入:::" + message);
                        break;

                    // 语音加入list？？？？ 暂不知道
                    case "VOICE_JOIN_LIST":
                        //					LOGGER.info("语音加入list:::" + message);
                        break;

                    // lol活动
                    case "LOL_ACTIVITY":
                        //					LOGGER.info("lol活动:::" + message);
                        break;

                    // 队伍礼物排名 目前只在6号lol房间抓取过
                    case "MATCH_TEAM_GIFT_RANK":
                        //					LOGGER.info("队伍礼物排名:::" + message);
                        break;

                    // 6.13端午节活动粽子新增活动更新命令 激活条件有人赠送活动礼物
                    case "ROOM_BANNER":
                        //					LOGGER.info("收到活动礼物赠送，更新信息:::" + message);
                        break;

                    // 设定房管消息 新房管的诞生
                    case "room_admin_entrance":
                        //					LOGGER.info("有人被设为了房管:::" + message);
                        break;

                    // 房管列表更新消息 激活条件为新房管的诞生
                    case "ROOM_ADMINS":
                        //					LOGGER.info("房管列表更新推送:::" + message);
                        break;

                    // 房间护盾 推测推送消息为破站官方屏蔽的关键字 触发条件未知
                    case "ROOM_SHIELD":
                        //					LOGGER.info("房间护盾触发消息:::" + message);
                        break;

                    // 主播开启房间全局禁言
                    case "ROOM_SILENT_ON":
                        //					LOGGER.info("主播开启房间全局禁言:::" + message);
                        break;

                    // 主播关闭房间全局禁言
                    case "ROOM_SILENT_OFF":
                        //					LOGGER.info("主播关闭房间全局禁言:::" + message);
                        break;

                    // 主播状态检测 直译 不知道什么情况 statue 1 ，2 ，3 ，4
                    case "ANCHOR_LOT_CHECKSTATUS":
                        //					LOGGER.info("主播房间状态检测:::" + message);
                        break;

                    // 房间警告消息 目前已知触发条件为 房间分区不正确
                    case "WARNING":
                        //					LOGGER.info("房间警告消息:::" + message);
                        break;
                    // 直播开启
                    case "LIVE":
                        GlobalSettingCache.LIVE_STATUS = 1;
                        //					room_id = jsonObject.getLong("roomid");
                        //					if (room_id == PublicDataConf.ROOMID) {
                        // 仅在直播有效 广告线程 改为配置文件
//                            settingService.holdSet(getCenterSetConf());
                        GlobalSettingCache.IS_ROOM_POPULARITY = true;
                        //					LOGGER.info("直播开启:::" + message);
                        break;

                    // 直播超管被切断
                    case "CUT_OFF":
                        //					LOGGER.info("很不幸，本房间直播被切断:::" + message);
                        break;

                    // 本房间已被封禁
                    case "ROOM_LOCK":
                        //					LOGGER.info("很不幸，本房间已被封禁:::" + message);
                        break;

                    // 直播准备中(或者是关闭直播)
                    case "PREPARING":
                        GlobalSettingCache.LIVE_STATUS = 0;
//                            settingService.holdSet(getCenterSetConf());
                        GlobalSettingCache.IS_ROOM_POPULARITY = false;
                        //					LOGGER.info("直播准备中(或者是关闭直播):::" + message);
                        break;

                    // 勋章亲密度达到上每日上限通知
                    case "LITTLE_TIPS":
                        //					LOGGER.info("勋章亲密度达到上每日上限:::" + message);
                        break;

                    // msg_type 1 为进入直播间 2 为关注 3为分享直播间
                    case "INTERACT_WORD":
                        // 关注
                        //控制台打印处理

                        msg_type = JSONObject.parseObject(messageJsonObject.getString("data")).getShort("msg_type");
                        if (msg_type == 2) {
                            interact = JSONObject.parseObject(messageJsonObject.getString("data"), Interact.class);
                            stringBuilder.append(JodaTimeUtils.formatDateTime(System.currentTimeMillis())).append(":新的关注:")
                                    .append(interact.getUname()).append(" 关注了直播间");
                            //控制台打印
                            if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
                                System.out.println(stringBuilder.toString());
                            }
                            //日志
                            if (GlobalSettingCache.logThread != null && !GlobalSettingCache.logThread.FLAG) {
                                GlobalSettingCache.logList.add(stringBuilder.toString());
                                synchronized (GlobalSettingCache.logThread) {
                                    GlobalSettingCache.logThread.notify();
                                }
                            }
                            //前端弹幕发送
                            try {
                                danmuWebsocket.sendMessage(WebsocketMessagePackage.toJson("follow", (short) 0, interact));
                            } catch (Exception e) {
                                // TODO 自动生成的 catch 块
                                e.printStackTrace();
                            }
                            stringBuilder.delete(0, stringBuilder.length());
                        }
                        //关注感谢
                        //欢迎进入直播间
                        //欢迎感谢


                        //打印测试用
                        msg_type = JSONObject.parseObject(messageJsonObject.getString("data")).getShort("msg_type");
                        if (msg_type != 3 && msg_type != 2 && msg_type != 1) {
                            log.info("直播间信息:::" + message);
                        }
                        break;
                    // 礼物bag bot
                    case "GIFT_BAG_DOT":
                        //					LOGGER.info("礼物bag" + message);
                        break;
                    case "ONLINERANK":
                        //					LOGGER.info("新在线排名更新信息推送:::" + message);
                        break;
                    case "ONLINE_RANK_COUNT":
                        //					LOGGER.info("在线排名人数更新信息推送:::" + message);
                        break;
                    case "ONLINE_RANK_V2":
                        //					LOGGER.info("在线排名v2版本信息推送(即高能榜:::" + message);
                        break;
                    case "ONLINE_RANK_TOP3":
                        //					LOGGER.info("在线排名前三信息推送(即高能榜:::" + message);
                        break;
                    case "HOT_RANK_CHANGED":
                        //					LOGGER.info("热门榜推送:::" + message);
                        break;
                    case "HOT_RANK_CHANGED_V2":
                        //					LOGGER.info("热门榜v2版本changed推送:::" + message);
                        break;
                    case "HOT_RANK_SETTLEMENT_V2":
                        //					LOGGER.info("热门榜v2版本set推送:::" + message);
                        break;
                    case "WIDGET_BANNER":
                        //					LOGGER.info("直播横幅广告推送:::" + message);
                        break;
                    case "MESSAGEBOX_USER_MEDAL_CHANGE":
                        //					LOGGER.info("本人勋章升级推送:::" + message);
                        break;
                    case "LIVE_INTERACTIVE_GAME":
                        //					LOGGER.info("互动游戏？？？推送:::" + message);
                        break;
                    case "WATCHED_CHANGE":
                        //{"cmd":"WATCHED_CHANGE","data":{"num":184547,"text_small":"18.4万","text_large":"18.4万人看过"}}
                        GlobalSettingCache.ROOM_WATCHER = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("num");
//                            LOGGER.info("多少人观看过:::" + message);
                        break;
                    case "STOP_LIVE_ROOM_LIST":
                        //					LOGGER.info("直播间关闭集合推送:::" + message);
                        break;
                    case "DANMU_AGGREGATION":
                        //					LOGGER.info("天选时刻条件是表情推送:::" + message);
                        break;
                    case "COMMON_NOTICE_DANMAKU":
                        //					LOGGER.info("警告信息推送（例如任务快完成之类的）:::" + message);
                        break;
                    case "POPULARITY_RED_POCKET_NEW":
                        //{"cmd":"POPULARITY_RED_POCKET_NEW",
                        // "data":{"lot_id":8677977,"start_time":1674572461,"current_time":1674572461,
                        // "wait_num":0,"uname":"直播小电视","uid":1407831746,"action":"送出",
                        // "num":1,"gift_name":"红包","gift_id":13000,"price":5000,"name_color":"",
                        // "medal_info":{"target_id":0,"special":"","icon_id":0,"anchor_uname":"",
                        // "anchor_roomid":0,"medal_level":0,"medal_name":"","medal_color":0,"medal_color_start":0,
                        // "medal_color_end":0,"medal_color_border":0,"is_lighted":0,"guard_level":0}}}
                        if (GlobalSettingCache.ALL_SETTING_CONF.is_gift()) {
                            redPackage = JSONObject.parseObject(messageJsonObject.getString("data"), RedPackage.class);
                            stringBuilder.append(JodaTimeUtils.formatDateTime(redPackage.getStart_time() * 1000));
                            stringBuilder.append(":收到红包:");
                            stringBuilder.append(redPackage.getUname());
                            stringBuilder.append(" ");
                            stringBuilder.append(redPackage.getAction());
                            stringBuilder.append("的:");
                            stringBuilder.append(redPackage.getGift_name());
                            stringBuilder.append(" x ");
                            stringBuilder.append(redPackage.getNum());
                            //控制台打印
                            if (GlobalSettingCache.ALL_SETTING_CONF.is_cmd()) {
                                System.out.println(stringBuilder.toString());
                            }
                            gift = new Gift();
                            gift.setGiftName(redPackage.getGift_name());
                            gift.setNum(redPackage.getNum());
                            gift.setPrice(redPackage.getPrice());
                            gift.setTotal_coin((long) redPackage.getNum() * redPackage.getPrice());
                            gift.setTimestamp(redPackage.getStart_time());
                            gift.setAction(redPackage.getAction());
                            gift.setCoin_type((short) 1);
                            gift.setUname(redPackage.getUname());
                            gift.setUid(redPackage.getUid());
                            gift.setMedal_info(redPackage.getMedal_info());
                            try {
                                danmuWebsocket.sendMessage(WebsocketMessagePackage.toJson("gift", (short) 0, gift));
                            } catch (Exception e) {
                                // TODO 自动生成的 catch 块
                                e.printStackTrace();
                            }
                            if (GlobalSettingCache.logThread != null && !GlobalSettingCache.logThread.FLAG) {
                                GlobalSettingCache.logList.add(stringBuilder.toString());
                                synchronized (GlobalSettingCache.logThread) {
                                    GlobalSettingCache.logThread.notify();
                                }
                            }
                            stringBuilder.delete(0, stringBuilder.length());
                        }

                        //					LOGGER.info("红包赠送:::" + message);
                        break;
                    case "POPULARITY_RED_POCKET_WINNER_LIST":
                        //					LOGGER.info("红包抽奖结果推送:::" + message);
                        break;
                    case "LIKE_INFO_V3_UPDATE":
//                            					LOGGER.info("点赞信息v3推送:::" + message);
                        //{"cmd":"LIKE_INFO_V3_UPDATE","data":{"click_count":371578}}
                        GlobalSettingCache.ROOM_LIKE = JSONObject.parseObject(messageJsonObject.getString("data")).getLong("click_count");
                        break;
                    case "LIKE_INFO_V3_CLICK":
                        //					LOGGER.info("点赞信息v3推送:::" + message);
                        break;
                    case "CORE_USER_ATTENTION":
                        //					LOGGER.info("中心用户推送:::" + message);
                        break;
                    case "HOT_RANK_SETTLEMENT":
                        //					LOGGER.info("热榜排名推送:::" + message);
                        break;
                    case "MESSAGEBOX_USER_GAIN_MEDAL":
                        //					LOGGER.info("粉丝勋章消息盒子推送:::" + message);
                        break;
                    case "POPULARITY_RED_POCKET_START":
//                        {"cmd":"POPULARITY_RED_POCKET_START", "data":{"lot_id":15279655,
//                                "sender_uid":300700903,"sender_name":"肉嘟嘟喽","sender_face":"https://i1.hdslb.com/bfs/face/3af2996893e1c77561fd9422e42bfb7a06292b23.jpg",
//                                "join_requirement":1,"danmu":"中奖喷雾！中奖喷雾！","current_time":1698408158,"start_time":1698408158,"end_time":1698408338,
//                                "last_time":180,"remove_time":1698408353,"replace_time":1698408348,"lot_status":1,
//                                "h5_url":"https://live.bilibili.com/p/html/live-app-red-envelope/popularity.html?is_live_half_webview=1\u0026hybrid_half_ui=1,5,100p,100p,000000,0,50,0,0,1;2,5,100p,100p,000000,0,50,0,0,1;3,5,100p,100p,000000,0,50,0,0,1;4,5,100p,100p,000000,0,50,0,0,1;5,5,100p,100p,000000,0,50,0,0,1;6,5,100p,100p,000000,0,50,0,0,1;7,5,100p,100p,000000,0,50,0,0,1;8,5,100p,100p,000000,0,50,0,0,1\u0026hybrid_rotate_d=1\u0026hybrid_biz=popularityRedPacket\u0026lotteryId=15279655",
//                                "user_status":2,"awards":[{"gift_id":31212,"gift_name":"打call","gift_pic":"https://s1.hdslb.com/bfs/live/461be640f60788c1d159ec8d6c5d5cf1ef3d1830.png",
//                                "num":2},{"gift_id":31214,"gift_name":"牛哇","gift_pic":"https://s1.hdslb.com/bfs/live/91ac8e35dd93a7196325f1e2052356e71d135afb.png","num":3},{"gift_id":31216,
//                                "gift_name":"小花花","gift_pic":"https://s1.hdslb.com/bfs/live/5126973892625f3a43a8290be6b625b5e54261a5.png","num":3}],"lot_config_id":3,"total_price":1600,"wait_num":7}}
//                        LOGGER.info("红包详细信息推送:::" + message);

                        break;
                    case "LITTLE_MESSAGE_BOX":
                        //					LOGGER.info("小消息box推送:::" + message);
                        break;
                    case "ANCHOR_HELPER_DANMU":
                        //					LOGGER.info("直播小助手信息推送:::" + message);
                        break;
                    case "ENTRY_EFFECT_MUST_RECEIVE":
                        //					LOGGER.info("直播小助手信息推送:::" + message);
                        break;
                    case "GIFT_STAR_PROCESS":
                        //					LOGGER.info("礼物开始进度条信息推送:::" + message);
                        break;
                    case "GUARD_HONOR_THOUSAND":
                        //					LOGGER.info("千舰推送:::" + message);
                        break;
                    case "FULL_SCREEN_SPECIAL_EFFECT":
                        //					LOGGER.info("FULL_SCREEN_SPECIAL_EFFECT:::" + message);
                        break;
                    case "CARD_MSG":
                        //					LOGGER.info("CARD_MSG:::" + message);
                        break;
                    case "USER_PANEL_RED_ALARM":
//                            					LOGGER.info("USER_PANEL_RED_ALARM:::" + message);
                        break;
                    case "TRADING_SCORE":
                        //					LOGGER.info("TRADING_SCORE:::" + message);
                        break;
                    case "USER_TASK_PROGRESS":
                        //					LOGGER.info("USER_TASK_PROGRESS:::" + message);
                        break;
                    case "POPULAR_RANK_CHANGED":
                        //					LOGGER.info("POPULAR_RANK_CHANGED:::" + message);
                        break;
                    case "AREA_RANK_CHANGED":
                        //					LOGGER.info("AREA_RANK_CHANGED:::" + message);
                        break;
                    case "PLAY_TAG":
                        //					LOGGER.info("PLAY_TAG:::" + message);
                        break;
                    case "PK_BATTLE_PROCESS_NEW":
                        //					LOGGER.info("PK_BATTLE_PROCESS_NEW:::" + message);
                        break;
                    case "PK_BATTLE_SETTLE_NEW":
                        //					LOGGER.info("PK_BATTLE_SETTLE_NEW:::" + message);
                        break;
                    case "PK_BATTLE_PUNISH_END":
                        //					LOGGER.info("PK_BATTLE_PUNISH_END:::" + message);
                        break;
                    case "PK_BATTLE_PRE_NEW":
                        //					LOGGER.info("PK_BATTLE_PRE_NEW:::" + message);
                        break;
                    case "PK_BATTLE_START_NEW":
                        //					LOGGER.info("PK_BATTLE_START_NEW:::" + message);
                        break;
                    case "INTERACTIVE_USER":
                        //					LOGGER.info("INTERACTIVE_USER:::" + message);
                        break;
                    default:
//                            LOGGER.info("其他未处理消息:" + message);
                        break;
                }
                    GlobalSettingCache.danmuList.remove(0);
                }

        } catch (Exception e) {
            log.error("解析弹幕出现异常,", e);
        }
    }



    public void DelayGiftTimeSetting() {
        synchronized (GlobalSettingCache.parsethankGiftThread) {
            if (GlobalSettingCache.parsethankGiftThread != null) {
            }
        }
    }



    public static String parseCmd(String cmd) {
        if(StringUtils.isBlank(cmd)) {
            return cmd;
        }
        if (cmd.startsWith("DANMU_MSG")) {
            return "DANMU_MSG";
        }
        if (cmd.startsWith("SEND_GIFT")) {
            return "SEND_GIFT";
        }
        if (cmd.startsWith("GUARD_BUY")) {
            return "GUARD_BUY";
        }
//		if(cmd.startsWith("SUPER_CHAT_MESSAGE")) {
//			return "SUPER_CHAT_MESSAGE";
//		}
//		if(cmd.startsWith("WELCOME")) {
//			return "WELCOME";
//		}
//		if(cmd.startsWith("WELCOME_GUARD")) {
//			return "WELCOME_GUARD";
//		}
//		if(cmd.startsWith("ROOM_RANK")) {
//			return "ROOM_RANK";
//		}
//		if(cmd.startsWith("ROOM_REAL_TIME_MESSAGE_UPDATE")) {
//			return "ROOM_REAL_TIME_MESSAGE_UPDATE";
//		}
//		if(cmd.startsWith("WARNING")) {
//			return "WARNING";
//		}
//		if(cmd.startsWith("LIVE")) {
//			return "LIVE";
//		}
//		if(cmd.startsWith("PREPARING")) {
//			return "PREPARING";
//		}

//		if(cmd.startsWith("SPECIAL_GIFT")) {
//			return "SPECIAL_GIFT";
//		}
//		if(cmd.startsWith("USER_TOAST_MSG")) {
//			return "USER_TOAST_MSG";
//		}
//		if(cmd.startsWith("NOTICE_MSG")) {
//			return "NOTICE_MSG";
//		}
//		if(cmd.startsWith("ANCHOR_LOT_START")) {
//			return "ANCHOR_LOT_START";
//		}
//		if(cmd.startsWith("ANCHOR_LOT_END")) {
//			return "ANCHOR_LOT_END";
//		}

//		
//		if(cmd.startsWith("COMBO_SEND")) {
//			return "COMBO_SEND";
//		}
//		if(cmd.startsWith("GUARD_LOTTERY_START")) {
//			return "GUARD_LOTTERY_START";
//		}
//		if(cmd.startsWith("SUPER_CHAT_MESSAGE_JPN")) {
//			return "SUPER_CHAT_MESSAGE_JPN";
//		}
//		if(cmd.startsWith("SUPER_CHAT_MESSAGE_DELETE")) {
//			return "SUPER_CHAT_MESSAGE_DELETE";
//		}
//		if(cmd.startsWith("ENTRY_EFFECT")) {
//			return "ENTRY_EFFECT";
//		}
//		if(cmd.startsWith("ACTIVITY_BANNER_UPDATE_V2")) {
//			return "ACTIVITY_BANNER_UPDATE_V2";
//		}
//		if(cmd.startsWith("ROOM_CHANGE")) {
//			return "ROOM_CHANGE";
//		}
//		if(cmd.startsWith("new_anchor_reward")) {
//			return "new_anchor_reward";
//		}
//		if(cmd.startsWith("HOUR_RANK_AWARDS")) {
//			return "HOUR_RANK_AWARDS";
//		}
//		if(cmd.startsWith("WISH_BOTTLE")) {
//			return "WISH_BOTTLE";
//		}
//		if(cmd.startsWith("RAFFLE_START")) {
//			return "RAFFLE_START";
//		}
//		if(cmd.startsWith("RAFFLE_END")) {
//			return "RAFFLE_END";
//		}
//		if(cmd.startsWith("ANCHOR_LOT_AWARD")) {
//			return "ANCHOR_LOT_AWARD";
//		}
//		if(cmd.startsWith("ANCHOR_NORMAL_NOTIFY")) {
//			return "ANCHOR_NORMAL_NOTIFY";
//		}
//		if(cmd.startsWith("WEEK_STAR_CLOCK")) {
//			return "WEEK_STAR_CLOCK";
//		}
//		if(cmd.startsWith("ROOM_BOX_MASTER")) {
//			return "ROOM_BOX_MASTER";
//		}
//		if(cmd.startsWith("ROOM_SKIN_MSG")) {
//			return "ROOM_SKIN_MSG";
//		}
//		if(cmd.startsWith("SYS_GIFT")) {
//			return "SYS_GIFT";
//		}
//		if(cmd.startsWith("ACTIVITY_MATCH_GIFT")) {
//			return "ACTIVITY_MATCH_GIFT";
//		}
//		if(cmd.startsWith("PK_BATTLE_ENTRANCE")) {
//			return "PK_BATTLE_ENTRANCE";
//		}
//		if(cmd.startsWith("PK_START")) {
//			return "PK_START";
//		}	
//		if(cmd.startsWith("PK_PRE")) {
//			return "PK_PRE";
//		}
//		if(cmd.startsWith("PK_MATCH")) {
//			return "PK_MATCH";
//		}
//		if(cmd.startsWith("PK_CLICK_AGAIN")) {
//			return "PK_CLICK_AGAIN";
//		}
//		if(cmd.startsWith("PK_MIC_END")) {
//			return "PK_MIC_END";
//		}
//		if(cmd.startsWith("PK_PROCESS")) {
//			return "PK_PROCESS";
//		}
//		if(cmd.startsWith("PK_SETTLE")) {
//			return "PK_SETTLE";
//		}
//		if(cmd.startsWith("PK_END")) {
//			return "PK_END";
//		}
//		if(cmd.startsWith("SYS_MSG")) {
//			return "SYS_MSG";
//		}
//		if(cmd.startsWith("GUARD_MSG")) {
//			return "GUARD_MSG";
//		}
//		if(cmd.startsWith("HOT_ROOM_NOTIFY")) {
//			return "HOT_ROOM_NOTIFY";
//		}
//		if(cmd.startsWith("room_admin_entrance")) {
//			return "room_admin_entrance";
//		}
//		if(cmd.startsWith("ROOM_ADMINS")) {
//			return "ROOM_ADMINS";
//		}
//		if(cmd.startsWith("ROOM_SHIELD")) {
//			return "ROOM_SHIELD";
//		}
//		if(cmd.startsWith("ROOM_SILENT_ON")) {
//			return "ROOM_SILENT_ON";
//		}
//		if(cmd.startsWith("ROOM_SILENT_OFF")) {
//			return "ROOM_SILENT_OFF";
//		}
//		if(cmd.startsWith("ANCHOR_LOT_CHECKSTATUS")) {
//			return "ANCHOR_LOT_CHECKSTATUS";
//		}
//		if(cmd.startsWith("VOICE_JOIN_ROOM_COUNT_INFO")) {
//			return "VOICE_JOIN_ROOM_COUNT_INFO";
//		}
//		if(cmd.startsWith("VOICE_JOIN_LIST")) {
//			return "VOICE_JOIN_LIST";
//		}
//		if(cmd.startsWith("")) {
//			return "";
//		}

        return cmd;
    }
}
