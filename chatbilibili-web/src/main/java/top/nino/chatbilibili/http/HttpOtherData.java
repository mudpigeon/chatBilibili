package top.nino.chatbilibili.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.nino.api.model.apex.ApexMessage;
import top.nino.api.model.apex.PredatorResult;
import top.nino.api.model.heart.XData;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.core.OkHttp3Utils;

import java.util.*;


public class HttpOtherData {
    private static Logger LOGGER = LogManager.getLogger(HttpOtherData.class);

    public static String httpGetNewEdition() {
        String data = null;
        JSONObject jsonObject = null;
        String edition = null;
        String code = "-1";
        Map<String, String> headers = null;
        Map<String, String> datas = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        datas = new HashMap<>(4);
        datas.put("roomid", GlobalSettingConf.centerSetConf.getRoomid().toString());
        datas.put("edition", GlobalSettingConf.VERSION);
        datas.put("time", String.valueOf(System.currentTimeMillis()));
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("http://bilibili.acproject.xyz/getEdition", headers, datas)
                    .body().string();
            if (data == null)
                return edition;
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                edition = ((JSONObject) jsonObject.get("result")).getString("value");
                GlobalSettingConf.NEW_VERSION = edition;
            } else {
                LOGGER.error("未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            edition = "获取公告失败";
            LOGGER.error("请求服务器超时，获取最新版本失败");
            data = null;
        }
        return edition;
    }

    public static String httpGetNewAnnounce() {
        String data = null;
        JSONObject jsonObject = null;
        String announce = null;
        String code = "-1";
        Map<String, String> headers = null;
        Map<String, String> datas = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        datas = new HashMap<>(4);
        datas.put("roomid", GlobalSettingConf.centerSetConf.getRoomid().toString());
        datas.put("edition", GlobalSettingConf.VERSION);
        datas.put("time", String.valueOf(System.currentTimeMillis()));
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("http://bilibili.acproject.xyz/getAnnounce", headers, datas)
                    .body().string();
            if (data == null)
                return announce;
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                announce = ((JSONObject) jsonObject.get("result")).getString("value");
            } else {
                LOGGER.error("未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            announce = "获取最新公告失败";
            LOGGER.error("请求服务器超时，获取最新公告失败");
            data = null;
        }
        return announce;
    }

    public static Long httpGetClockInRecord() {
        String data = null;
        JSONObject jsonObject = null;
        Long uid = null;
        String code = "-1";
        Map<String, String> headers = null;
        Map<String, String> datas = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        datas = new HashMap<>(4);
        datas.put("uid", GlobalSettingConf.USER.getUid().toString());
        datas.put("edition", GlobalSettingConf.VERSION);
        datas.put("time", String.valueOf(System.currentTimeMillis()));
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("http://bilibili.acproject.xyz/getClockRecord", headers, datas)
                    .body().string();
            if (data == null)
                return null;
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                if (jsonObject.get("result") != null) {
                    uid = ((JSONObject) jsonObject.get("result")).getLong("uid");
                }
            } else {
                LOGGER.error("未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            uid = null;
            LOGGER.error("请求服务器超时，获取最新打卡记录失败");
            data = null;
        }
        return uid;
    }

    public static Long httpPOSTSetClockInRecord() {
        String data = null;
        JSONObject jsonObject = null;
        Long uid = null;
        String code = "-1";
        Map<String, String> headers = null;
        Map<String, String> datas = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        datas = new HashMap<>(4);
        datas.put("uid", GlobalSettingConf.USER.getUid().toString());
        datas.put("edition", GlobalSettingConf.VERSION);
        datas.put("time", String.valueOf(System.currentTimeMillis()));
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpPostForm("http://bilibili.acproject.xyz/setClockRecord", headers, datas)
                    .body().string();
            if (data == null)
                return null;
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                if (jsonObject.get("result") != null) {
                    uid = ((JSONObject) jsonObject.get("result")).getLong("uid");
                }
            } else {
                LOGGER.error("未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            uid = null;
            LOGGER.error("请求服务器超时，获取最新打卡记录失败");
            data = null;
        }

        return uid;
    }


    @Deprecated
    public static String httpGetIp() {
        String data = null;
        JSONObject jsonObject = null;
        String status = null;
        String ip = null;
        Map<String, String> headers = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("http://ip-api.com/json/", headers, null)
                    .body().string();
            if (data == null)
                return null;
            jsonObject = JSONObject.parseObject(data);
            try {
                status = jsonObject.getString("status");
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (StringUtils.isBlank(status)) {
                return "获取失败:请自行获取本机对公Ip地址";
            }
            if (status.equals("success")) {
                ip = jsonObject.getString("query");
            } else {
                LOGGER.error("获取ip失败" + jsonObject.toString());
            }
        } catch (Exception e) {
            ip = "获取失败:请自行获取本机对公Ip地址";
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            LOGGER.error(ip);
            data = null;
        }
        return ip;
    }

    public static String httpGetIpV2() {
        String data = "";
        JSONObject jsonObject = null;
        String code = "-1";
        PredatorResult predatorResult = null;
        Map<String, String> headers = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("http://bilibili.acproject.xyz/ip", headers, null)
                    .body().string();
            if (data == null)
                return "";
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                data = jsonObject.getString("result");
            } else {
                LOGGER.error("ip获取： 未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            LOGGER.error("请求服务器超时，获取ip失败");
            data = "";
        }
        return data;
    }


    public static String httpPostEncsUrl() {
        String data = null;
        JSONObject jsonObject = null;
        String url = null;
        String code = "-1";
        Map<String, String> headers = null;
        Map<String, String> datas = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        datas = new HashMap<>(4);
        datas.put("roomid", GlobalSettingConf.centerSetConf.getRoomid().toString());
        datas.put("edition", GlobalSettingConf.VERSION);
        datas.put("time", String.valueOf(System.currentTimeMillis()));
        try {
            data = OkHttp3Utils.getHttp3Utils()
                    .httpPostForm("http://bilibili.acproject.xyz/getEncsServer", headers, datas)
                    .body().string();
            if (data == null)
                return url;
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                url = jsonObject.getString("result");
                GlobalSettingConf.SMALLHEART_ADRESS = url;
            } else {
                LOGGER.error("未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            url = null;
            LOGGER.error("请求服务器超时，获取服务器链接失败");
            data = null;
        }

        return url;
    }

    /**
     * 加密s函数方法来自 https://github.com/lkeme/bilibili-pcheartbeat
     * 服务器来自 https://github.com/lkeme/BiliHelper-personal
     *
     * @param xData
     * @param ts
     * @return
     */
    public static String httpPostencS(XData xData, long ts) {
        String data = null;
        JSONObject jsonObject = null;
        String s = null;
        String url = GlobalSettingConf.SMALLHEART_ADRESS;
        if (StringUtils.isBlank(url)) {
            return null;
        }
        Map<String, String> headers = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        JSONObject t = new JSONObject();
        t.put("id", xData.getId());
        t.put("device", xData.getDevice());
        t.put("ets", xData.getEts());
        t.put("benchmark", xData.getBenchmark());
        t.put("time", xData.getTime());
        t.put("ts", ts);
        t.put("ua", xData.getUa());
        JSONObject json = new JSONObject();
        json.put("t", t);
        json.put("r", xData.getSecret_rule());
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            data = OkHttp3Utils.getHttp3Utils().httpPostJson(url, headers, json.toJSONString()).body().string();
            if (data == null)
                return null;
            jsonObject = JSONObject.parseObject(data);
            try {
                s = jsonObject.getString("s");
            } catch (Exception e) {
                LOGGER.error("加密s错误");
                // TODO: handle exception
                s = null;
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error("连接至加密服务器错误？不存在");
            data = null;
            s = null;
//			e.printStackTrace();
        }

        return s;
    }





    //apex 0PC排位大逃杀数据 1PC排位竞技场 3PS4大逃杀 4PS4竞技场

    public static PredatorResult httpGetApexPredator(String key, String type) {
        String data = null;
        JSONObject jsonObject = null;
        String code = "-1";
        PredatorResult predatorResult = null;
        Map<String, String> headers = null;
        Map<String, String> datas = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        datas = new HashMap<>(5);
        if (StringUtils.isNotBlank(key)) {
            datas.put("key", key);
        }
        if (StringUtils.isNotBlank(type)) {
            datas.put("type", type);
        }
        try {
            datas.put("edition", GlobalSettingConf.VERSION);
            datas.put("time", String.valueOf(System.currentTimeMillis()));
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("http://bilibili.acproject.xyz/apex_banked", headers, datas)
                    .body().string();
            if (data == null)
                return predatorResult;
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                predatorResult = jsonObject.getObject("result", PredatorResult.class);
            } else {
                LOGGER.error("未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            LOGGER.error("请求服务器超时，获取apex信息失败");
            data = null;
        }
        return predatorResult;
    }

    public static ApexMessage httpGetApexMessage() {
        String data = null;
        JSONObject jsonObject = null;
        String code = "-1";
        ApexMessage apexMessage = null;
        Map<String, String> headers = null;
        Map<String, String> datas = null;
        headers = new HashMap<>(2);
        headers.put("user-agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        datas = new HashMap<>(3);
        try {
            datas.put("edition", GlobalSettingConf.VERSION);
            datas.put("time", String.valueOf(System.currentTimeMillis()));
            data = OkHttp3Utils.getHttp3Utils()
                    .httpGet("http://bilibili.acproject.xyz/apex_message", headers, datas)
                    .body().string();
            if (data == null)
                return apexMessage;
            jsonObject = JSONObject.parseObject(data);
            code = jsonObject.getString("code");
            if (code.equals("200")) {
                apexMessage = jsonObject.getObject("result", ApexMessage.class);
            } else {
                LOGGER.error("未知错误,原因:" + jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            LOGGER.error(e);
            LOGGER.error("请求服务器超时，获取apex总信息失败");
        }
        return apexMessage;
    }
}
