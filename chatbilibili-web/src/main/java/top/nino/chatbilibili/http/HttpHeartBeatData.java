package top.nino.chatbilibili.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.nino.api.model.danmu.RoomInfo;
import top.nino.api.model.heart.SmallHeart;
import top.nino.api.model.heart.XData;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.tool.CurrencyTools;
import top.nino.core.http.OkHttp3Utils;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class HttpHeartBeatData {
	private static Logger LOGGER = LogManager.getLogger(HttpHeartBeatData.class);

	public static void httpGetHeartBeatOrS(Long timestamp) {
		String data = null;
		JSONObject jsonObject = null;
		String urlString = null;
		if (timestamp != null) {
			urlString = "https://api.live.bilibili.com/relation/v1/Feed/heartBeat?_=" + timestamp;
		} else {
			urlString = "https://api.live.bilibili.com/relation/v1/Feed/heartBeat";
		}
		Map<String, String> headers = null;
		headers = new HashMap<>(4);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {
			headers.put("cookie", GlobalSettingCache.COOKIE_VALUE);
		}
		try {
			data = OkHttp3Utils.getHttp3Utils().httpGet(urlString, headers, null).body().string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return;

		jsonObject = JSONObject.parseObject(data);
		short code = jsonObject.getShort("code");
		if (code == 0) {
			if (jsonObject.getString("msg").equals("success")) {
//				LOGGER.info("在线心跳包get发送成功"+jsonObject.getString("data"));
			} else {
				LOGGER.error("在线心跳包get发送失败" + jsonObject.toString());
			}
		} else {
			LOGGER.error("在线心跳包get发送失败" + jsonObject.toString());
		}
	}

	public static void httpPostUserOnlineHeartBeat() {
		JSONObject jsonObject = null;
		String data = null;
		Map<String, String> headers = null;
		Map<String, String> params = null;
		if (GlobalSettingCache.USER_BARRAGE_MESSAGE == null && GlobalSettingCache.USER_COOKIE_INFO == null)
			return;
		headers = new HashMap<>(4);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" +  CurrencyTools.parseRoomId());
		if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {
			headers.put("cookie", GlobalSettingCache.COOKIE_VALUE);
		}
		params = new HashMap<>(4);
		params.put("csrf_token", GlobalSettingCache.USER_COOKIE_INFO.getBili_jct());
		params.put("csrf", GlobalSettingCache.USER_COOKIE_INFO.getBili_jct());
		params.put("visit_id", "");
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpPostForm("https://api.live.bilibili.com/User/userOnlineHeart", headers, params).body()
					.string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return;
		jsonObject = JSONObject.parseObject(data);
		short code = jsonObject.getShort("code");
		if (code == 0) {
			if (jsonObject.getString("message").equals("0")) {
//				LOGGER.info("心跳包post发送成功" + jsonObject.getString("data"));
			} else {
				LOGGER.error("心跳包post发送失败,未知错误,原因未知v" + jsonObject.toString());
			}
		} else {
			LOGGER.error("发跳包post发送失败,未知错误,原因未知" + jsonObject.toString());
		}
	}

	/**
	 * @param roomInfo
	 * @return
	 */
	public static XData httpPostE(RoomInfo roomInfo) {
		JSONObject jsonObject = null;
		String data = null;
		Map<String, String> headers = null;
		LinkedHashMap<String, String> params = new LinkedHashMap<>();
		SmallHeart smallHeart=null;
		XData xData = null;
		StringBuilder stringBuilder = new StringBuilder(50);
		if (GlobalSettingCache.USER_BARRAGE_MESSAGE == null && GlobalSettingCache.USER_COOKIE_INFO == null)
			return null;
		headers = new HashMap<>(4);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {
			headers.put("cookie", GlobalSettingCache.COOKIE_VALUE);
		}
		long[] ids = {roomInfo.getParent_area_id(),roomInfo.getArea_id(),0, GlobalSettingCache.ROOM_ID};
		stringBuilder.append("[")
				.append("\"").append(CurrencyTools.deviceHash()).append("\"").append(",")
				.append("\"").append(CurrencyTools.getUUID()).append("\"").append("]");
		String devices =stringBuilder.toString();
		params = new LinkedHashMap<>(10);
		long ts =System.currentTimeMillis();
		params.put("id", Arrays.toString(ids));
		params.put("device",devices);
		params.put("ts", String.valueOf(ts));
		params.put("is_patch", "0");
		params.put("heart_beat", "[]");
		params.put("ua", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		params.put("csrf_token", GlobalSettingCache.USER_COOKIE_INFO.getBili_jct());
		params.put("csrf", GlobalSettingCache.USER_COOKIE_INFO.getBili_jct());
		params.put("visit_id", "");
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpPostForm("https://live-trace.bilibili.com/xlive/data-interface/v1/x25Kn/E", headers, params).body()
					.string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return null;
		jsonObject = JSONObject.parseObject(data);
		short code = jsonObject.getShort("code");
		if (code == 0) {
			if (jsonObject.getString("message").equals("0")) {
//				LOGGER.info("心跳包post发送成功" + jsonObject.getString("data"));
				smallHeart  = jsonObject.getObject("data", SmallHeart.class);
				xData = new XData(ids,devices, smallHeart.getTimestamp(), smallHeart.getSecret_key(), smallHeart.getHeartbeat_interval(), ts,smallHeart.getSecret_rule(),false);
			} else{
				LOGGER.error("心跳包Epost发送失败,未知错误,原因未知v:" + jsonObject.toString());
			}
		} else {
			LOGGER.error("发跳包Epost发送失败,未知错误,原因未知:" + jsonObject.toString());
		}
		stringBuilder.delete(0, stringBuilder.length());
		return xData;
	}

	/**
	 * 加密s函数方法来自 https://github.com/lkeme/bilibili-pcheartbeat
	 *
	 * @param roomInfo
	 * @param num
	 * @param xData
	 * @return
	 */
	public static XData httpPostX(RoomInfo roomInfo,int num,XData xData) {

		JSONObject jsonObject = null;
		String data = null;
		Map<String, String> headers = null;
		Map<String, String> params = null;
		SmallHeart smallHeart=null;
		if (GlobalSettingCache.USER_BARRAGE_MESSAGE == null && GlobalSettingCache.USER_COOKIE_INFO == null)
			return xData;
		headers = new HashMap<>(4);
		headers.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		headers.put("referer", "https://live.bilibili.com/" + CurrencyTools.parseRoomId());
		if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {
			headers.put("cookie", GlobalSettingCache.COOKIE_VALUE);
		}
		params = new HashMap<>(12);
		long[] ids = {roomInfo.getParent_area_id(),roomInfo.getArea_id(),num, GlobalSettingCache.ROOM_ID};
//		String[] devices = {CurrencyTools.deviceHash(),CurrencyTools.getUUID()};
		long ts =System.currentTimeMillis();
		xData.setId(ids);
		// s参数处理
		final CompletableFuture<String> completableFuture =CompletableFuture.supplyAsync(()->{
			String s = null;
			long startTime = xData.getStartTime();
			//减去60秒
			if(startTime<=0)startTime=System.currentTimeMillis();
			while(StringUtils.isBlank(s)) {
				//超时处理
				if(System.currentTimeMillis()-startTime>= (xData.getTime()*1000))break;
				s = HttpOtherData.httpPostencS(xData, ts);
			}
			return s;
		});
		try {
			params.put("s", completableFuture.get());
		} catch (InterruptedException e) {
			//	e.printStackTrace();
			LOGGER.error("s 函数处理错误1:{}",e);
		} catch (ExecutionException e) {
			//	e.printStackTrace();
			LOGGER.error("s 函数处理错误2:{}",e);
		}
		params.put("id", Arrays.toString(ids));
		params.put("device",  xData.getDevice());
		params.put("ets",xData.getEts().toString());
		params.put("benchmark",xData.getBenchmark());
		params.put("time", xData.getTime().toString());
		params.put("ts", String.valueOf(ts));
		params.put("ua", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
		params.put("csrf_token", GlobalSettingCache.USER_COOKIE_INFO.getBili_jct());
		params.put("csrf", GlobalSettingCache.USER_COOKIE_INFO.getBili_jct());
		params.put("visit_id", "");
		try {
			data = OkHttp3Utils.getHttp3Utils()
					.httpPostForm("https://live-trace.bilibili.com/xlive/data-interface/v1/x25Kn/X", headers, params).body()
					.string();
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			LOGGER.error(e);
			data = null;
		}
		if (data == null)
			return xData;
		jsonObject = JSONObject.parseObject(data);
		short code = jsonObject.getShort("code");
		if (code == 0) {
			if (jsonObject.getString("message").equals("0")) {
				smallHeart  = jsonObject.getObject("data", SmallHeart.class);
				xData.setTs(ts);
				xData.setBenchmark(smallHeart.getSecret_key());
				xData.setEts(smallHeart.getTimestamp());
				xData.setSecret_rule(smallHeart.getSecret_rule());
				xData.setTime(smallHeart.getHeartbeat_interval());
				xData.setError(false);
				//LOGGER.info("心跳包post发送成功,消息体:{},返回组合体:{}",jsonObject.getString("data"),xData);
			} else {
				LOGGER.error("心跳包Xpost发送失败,未知错误,原因未知v:{},错误体:{}",jsonObject.toString(),xData);
				xData.setError(true);
			}
		} else {
			LOGGER.error("发跳包Xpost发送失败,未知错误,原因未知:{},错误体:{}",jsonObject.toString(),xData);
			xData.setError(true);
		}
		return xData;
	}
}
