package top.nino.chatbilibili;

import org.springframework.context.annotation.Configuration;
import top.nino.api.model.auto_reply.AutoReply;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.chatbilibili.conf.base.CenterSetConf;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.danmu.Interact;
import top.nino.api.model.enums.LiveStatusEnum;
import top.nino.api.model.room.MedalInfoAnchor;
import top.nino.api.model.user.AutoSendGift;
import top.nino.api.model.user.User;
import top.nino.api.model.user.UserManager;
import top.nino.chatbilibili.data.user_in_room_barrageMsg.UserBarrageMsg;
import top.nino.chatbilibili.client.WebSocketProxy;
import top.nino.chatbilibili.thread.*;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
public class GlobalSettingConf {

	//------------- 1.基本预设好的常量-----------开始----------------------
	public final static String GLOBAL_SETTING_FILE_NAME = "chatBilibili-globalSetting";
	public final static String FILE_COOKIE_PREFIX = "validCookie";
	public final static String FILE_SETTING_PREFIX = "setting";

	//------------- 基本预设好的常量-----------结束----------------------


	//------------- 2.运行中加载/缓存的数据-----------开始----------------------

	// 设置
	public static CenterSetConf centerSetConf;

	// cookie String串
	public static String COOKIE_VALUE;

	// cookie parse 解析后 的最小有效pojo存储（还没有进行网络验证该cookie是否有效）
	public static UserCookieInfo USER_COOKIE_INFO;

	// user信息（应该是验证cookie有效后才有数值）
	public static User USER;

	// 房间号
	public static Long ROOMID;

	// 短号
	public static Integer SHORTROOMID = null;

	// 主播uid
	public static Long AUID = null;

	// user弹幕长度
	public static UserBarrageMsg USER_BARRAGE_MESSAGE;

	//websocket客户端主线程
	public static WebSocketProxy webSocketProxy;

	//------------- 运行中加载/缓存的数据-----------结束----------------------


	// url 直播弹幕websocket地址
	public static String URL = "wss://broadcastlv.chat.bilibili.com:2245/sub";


	// 主播粉丝数
	public static Long FANSNUM =null;

	// 主播名称
	public static String ANCHOR_NAME = null;

	// 主播勋章信息
	public static MedalInfoAnchor MEDALINFOANCHOR = null;

	// 房间人气
	public static Long ROOM_POPULARITY =1L;

	// 房间观看人数（历史）
	public static Long ROOM_WATCHER = 0L;

	// 点赞数量
	public static Long ROOM_LIKE = 0L;

	// 直播状态 0不直播 1直播 2轮播
	public static Integer lIVE_STATUS = LiveStatusEnum.CLOSED.getCode();



	// user房间管理信息
	public static UserManager USERMANAGER = null;

	// 天选礼物屏蔽
	public static String SHIELDGIFTNAME = null;

	// 天选是否正在屏蔽关注
	public static Boolean ISSHIELDFOLLOW = false;

	// 天选是否正在屏蔽欢迎
	public static Boolean ISSHIELDWELCOME = false;


	
	//心跳包 16进制
	public final static String heartByte="0000001f0010000100000002000000015b6f626a656374204f626a6563745d";
	//包头长
	public final static char packageHeadLength = 16;

	//验证包协议类型
	public final static int firstPackageType = 7;

	//心跳包协议类型
	public final static int heartPackageType = 2;

	//心跳包&验证包协议版本
	public final static char packageVersion = 1;

    //心跳包&验证包的尾巴其他
	public final static int packageOther = 1;
	

	//心跳线程
	public static HeartByteThread heartByteThread;

	//重新连接线程
	public static ReConnThread reConnThread;

	//处理信息分类线程
	public static ParseMessageThread parseMessageThread;

	//处理弹幕包集合
	public final static Vector<String> resultStrs = new Vector<String>(100);

	//礼物感谢集
	public final static Map<String, Vector<Gift>> thankGiftConcurrentHashMap = new ConcurrentHashMap<String,Vector<Gift>>(3000);

	//待发弹幕集
	public final static Vector<String> barrageString = new Vector<String>();

	//log日志待写入集合
	public final static Vector<String> logString = new Vector<String>(100);

	//待发送感谢关注集合
	public final static Vector<Interact> interacts = new Vector<Interact>(200);

	//待发送欢迎进入直播间集合
	public final static Vector<Interact> interactWelcome = new Vector<Interact>(400);

	//自动回复处理弹幕
	public final static Vector<AutoReply> replys = new Vector<AutoReply>();
	
	//日志线程
	public static LogThread logThread;

	//处理感谢关注线程
	public static ParseThankFollowThread parsethankFollowThread = new ParseThankFollowThread();

	//处理感谢进入直播间线程
	public static ParseThankWelcomeThread parseThankWelcomeThread = new ParseThankWelcomeThread();

	//广告姬线程
	public static AdvertThread advertThread;

	//感谢礼物数据集线程
	public static ParseThankGiftThread parsethankGiftThread = new ParseThankGiftThread();

	//发送弹幕线程
	public static SendBarrageThread sendBarrageThread;

	//屏蔽天选礼物线程
	public static GiftShieldThread giftShieldThread = new GiftShieldThread();

	//屏蔽天选关注线程
	public static FollowShieldThread followShieldThread = new FollowShieldThread();

	//屏蔽天选欢迎线程
	public static WelcomeShieldThread welcomeShieldThread = new WelcomeShieldThread();

	//自动回复线程
	public static AutoReplyThread autoReplyThread;
	
	//用户在线线程集
	public static HeartBeatThread heartBeatThread;

	public static HeartBeatsThread heartBeatsThread;

	public static UserOnlineHeartThread userOnlineHeartThread;

	//小心心线程
	public static SmallHeartThread smallHeartThread;

	//签到线程

	//是否显示人气
	public static Boolean IS_ROOM_POPULARITY =false;
	
	//task
//	public static SchedulingRunnableUtil dosigntask = null;
	
	public static Long ROOMID_LONG = null;

	public static String SMALLHEART_ADRESS = null;

	public static boolean is_sign= false;

	public static String ANNOUNCE = null;







	public static boolean INIT_CHECK_EDITION = false;

	public static boolean INIT_CHECK_ANNOUNCE = false;

	public static int manager_login_size=0;

//	//view
//	//房间礼物集合
//	public static Map<Integer, RoomGift> roomGiftConcurrentHashMap = new ConcurrentHashMap<Integer, RoomGift>(300);
//
	//可以赠送礼物集合 要初始化
	public static Map<Integer, AutoSendGift> autoSendGiftMap = null;

	//测试模式
	public static boolean TEST_MODE = false;

	//方法区

	public static void init_user(){
		GlobalSettingConf.USER_COOKIE_INFO = null;
		GlobalSettingConf.USER = null;
		GlobalSettingConf.COOKIE_VALUE = null;
		GlobalSettingConf.USER_BARRAGE_MESSAGE = null;
	}

	public static void init_send(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.thankGiftConcurrentHashMap.clear();
		GlobalSettingConf.barrageString.clear();
		GlobalSettingConf.interacts.clear();
		GlobalSettingConf.interactWelcome.clear();
	}

	public static void init_all(){
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.resultStrs.clear();
		GlobalSettingConf.thankGiftConcurrentHashMap.clear();
		GlobalSettingConf.barrageString.clear();
		GlobalSettingConf.logString.clear();
		GlobalSettingConf.interacts.clear();
		GlobalSettingConf.interactWelcome.clear();
		GlobalSettingConf.SHIELDGIFTNAME = null;
		GlobalSettingConf.ISSHIELDFOLLOW = false;
		GlobalSettingConf.ISSHIELDWELCOME = false;
	}

	public static void init_connect(){
		GlobalSettingConf.SHIELDGIFTNAME = null;
		GlobalSettingConf.replys.clear();
		GlobalSettingConf.resultStrs.clear();
		GlobalSettingConf.thankGiftConcurrentHashMap.clear();
		GlobalSettingConf.barrageString.clear();
		GlobalSettingConf.interacts.clear();
		GlobalSettingConf.logString.clear();
		GlobalSettingConf.interactWelcome.clear();
		GlobalSettingConf.ISSHIELDWELCOME=false;
		GlobalSettingConf.ISSHIELDFOLLOW=false;
		GlobalSettingConf.ROOMID = null;
		GlobalSettingConf.ANCHOR_NAME = null;
		GlobalSettingConf.AUID = null;
		GlobalSettingConf.FANSNUM = null;
		GlobalSettingConf.SHORTROOMID = null;
		GlobalSettingConf.lIVE_STATUS = 0;
		GlobalSettingConf.ROOM_POPULARITY = 1L;
	}


}
