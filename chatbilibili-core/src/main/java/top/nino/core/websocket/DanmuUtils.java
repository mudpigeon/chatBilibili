package top.nino.core.websocket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.server.DanmuServerInfo;

import java.util.List;

/**
 * @author : nino
 * @date : 2024/2/7 04:35
 */
@Slf4j
public class DanmuUtils {

    /**
     * 获取随机破站弹幕服务器地址 20201218优化获取
     *
     * @param danmuServerInfoList
     * @return
     */
    public static String randomGetWebsocketUrl(String preWebsocketUrl,  List<DanmuServerInfo> danmuServerInfoList) {
        StringBuilder newWebsocketUrl = new StringBuilder();
        String websocketUrl = null;
        int control = 0;
        if (danmuServerInfoList.size() > 0) {
            while (StringUtils.isBlank(preWebsocketUrl) ||  !(preWebsocketUrl).equals(websocketUrl)) {
                if (control > 5) {
                    break;
                }
                DanmuServerInfo danmuServerInfo = danmuServerInfoList.get((int) (Math.random() * danmuServerInfoList.size()));
                newWebsocketUrl.append("wss://")
                        .append(danmuServerInfo.getHost())
                        .append(":")
                        .append(danmuServerInfo.getWss_port())
                        .append("/sub");
                websocketUrl = newWebsocketUrl.toString();
                newWebsocketUrl.delete(0, newWebsocketUrl.length());
                control++;
            }
        }
        log.info("获取破站弹幕服务器websocket地址：{}", websocketUrl);
        return websocketUrl;

    }
}
