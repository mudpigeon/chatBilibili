package top.nino.chatbilibili;

import lombok.ToString;
import org.springframework.context.annotation.Configuration;
import top.nino.api.model.auto_reply.AutoReply;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.chatbilibili.conf.base.AllSettingConfig;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.enums.LiveStatusEnum;
import top.nino.api.model.room.AnchorMedalInfo;
import top.nino.api.model.user.AutoSendGift;
import top.nino.api.model.user.User;
import top.nino.api.model.user.UserManager;
import top.nino.api.model.user_in_room_barrageMsg.UserBarrageMsg;
import top.nino.chatbilibili.client.BilibiliWebSocketProxy;
import top.nino.chatbilibili.thread.*;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author nino
 */
@ToString
@Configuration
public class GlobalSettingConf {

	//------------- 1.基本预设好的常量-----------开始----------------------
	public final static String GLOBAL_SETTING_FILE_NAME = "chatBilibili-globalSetting";
	public final static String FILE_COOKIE_PREFIX = "validCookie";
	public final static String FILE_SETTING_PREFIX = "setting";

	// 包头长
	public final static char PACKAGE_HEAD_LENGTH = 16;

	// 心跳包&验证包协议版本
	public final static char PACKAGE_VERSION = 1;

	// 验证包协议类型
	public final static int FIRST_PACKAGE_TYPE = 7;

	// 心跳包 16进制
	public final static String HEART_BYTE = "0000001f0010000100000002000000015b6f626a656374204f626a6563745d";

	//-------------1.基本预设好的常量-----------结束----------------------







	//------------- 2.运行中加载/缓存的数据-----------开始----------------------

	// 设置
	public static AllSettingConfig ALL_SETTING_CONF;

	// cookie String串
	public static String COOKIE_VALUE;

	// cookie parse 解析后 的最小有效pojo存储（还没有进行网络验证该cookie是否有效）
	public static UserCookieInfo USER_COOKIE_INFO;

	// user信息（应该是验证cookie有效后才有数值）
	public static User USER;

	// 房间号
	public static Long ROOM_ID;

	// 房间长号
	public static Long ROOMID_LONG;

	// 房间短号
	public static Long SHORT_ROOM_ID;

	//是否显示人气
	public static Boolean IS_ROOM_POPULARITY;

	// 主播uid
	public static Long ANCHOR_UID;

	// 主播名称
	public static String ANCHOR_NAME;

	// 粉丝数
	public static Long FANS_NUM;

	// 房间人气
	public static Long ROOM_POPULARITY;

	// 直播状态 0不直播 1直播 2轮播
	public static Integer LIVE_STATUS = LiveStatusEnum.CLOSED.getCode();

	// 主播勋章信息
	public static AnchorMedalInfo ANCHOR_MEDAL_INFO;

	// url 直播弹幕websocket地址
	public static String ROOM_DANMU_WEBSOCKET_URL = "wss://broadcastlv.chat.bilibili.com:2245/sub";

	// user弹幕长度
	public static UserBarrageMsg USER_BARRAGE_MESSAGE;

	// user房间管理信息
	public static UserManager USER_MANAGER;


	//------------- 2.运行中加载/缓存的数据-----------结束----------------------




	//------------- 3.行为-----------开始----------------------

	public static void clearUserCache(){
		GlobalSettingConf.COOKIE_VALUE = null;
		GlobalSettingConf.USER_COOKIE_INFO = null;
		GlobalSettingConf.USER = null;
		GlobalSettingConf.USER_BARRAGE_MESSAGE = null;
	}


	//------------- 3.行为-----------结束----------------------






	//------------- 4.线程----------开始----------------------

	// 处理弹幕包集合
	public final static Vector<String> danmuList = new Vector<>(100);

	// log日志待写入集合
	public final static Vector<String> logList = new Vector<>(100);

	// websocket客户端主线程
	public static BilibiliWebSocketProxy bilibiliWebSocketProxy;

	// 心跳线程
	public static HeartCheckBilibiliDanmuServerThread heartCheckBilibiliDanmuServerThread;

	// 处理信息分类线程
	public static ParseDanmuMessageThread parseDanmuMessageThread;

	// 日志线程
	public static LogThread logThread;

	// 重新连接线程
	public static ReConnThread reConnThread;

	//------------- 4.线程-----------结束----------------------





	// 房间观看人数（历史）
	public static Long ROOM_WATCHER = 0L;

	// 点赞数量
	public static Long ROOM_LIKE = 0L;

	//心跳包协议类型
	public final static int heartPackageType = 2;

	//心跳包&验证包的尾巴其他
	public final static int packageOther = 1;




	//礼物感谢集
	public final static Map<String, Vector<Gift>> thankGiftConcurrentHashMap = new ConcurrentHashMap<String,Vector<Gift>>(3000);

	//待发弹幕集
	public final static Vector<String> barrageString = new Vector<String>();


	//自动回复处理弹幕
	public final static Vector<AutoReply> replys = new Vector<AutoReply>();

	//感谢礼物数据集线程
	public static ParseThankGiftThread parsethankGiftThread = new ParseThankGiftThread();

	//发送弹幕线程
	public static SendBarrageThread sendBarrageThread;

	//用户在线线程集
	public static HeartBeatThread heartBeatThread;

	public static HeartBeatsThread heartBeatsThread;

	public static UserOnlineHeartThread userOnlineHeartThread;

	//小心心线程
	public static SmallHeartThread smallHeartThread;

	public static String SMALLHEART_ADRESS = null;

	public static boolean is_sign= false;


	public static int manager_login_size=0;


	//可以赠送礼物集合 要初始化
	public static Map<Integer, AutoSendGift> autoSendGiftMap = null;

	//测试模式
	public static boolean TEST_MODE = false;


	public static void init_send(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.thankGiftConcurrentHashMap.clear();
		GlobalSettingConf.barrageString.clear();
	}

	public static void init_all(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.danmuList.clear();
		GlobalSettingConf.thankGiftConcurrentHashMap.clear();
		GlobalSettingConf.barrageString.clear();
		GlobalSettingConf.logList.clear();

	}

	public static void init_connect(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.danmuList.clear();
		GlobalSettingConf.barrageString.clear();
		GlobalSettingConf.logList.clear();
		GlobalSettingConf.ROOM_ID = null;
		GlobalSettingConf.ANCHOR_NAME = null;
		GlobalSettingConf.ANCHOR_UID= null;
		GlobalSettingConf.FANS_NUM = null;
		GlobalSettingConf.SHORT_ROOM_ID = null;
		GlobalSettingConf.LIVE_STATUS = 0;
		GlobalSettingConf.ROOM_POPULARITY = 1L;
	}


}
