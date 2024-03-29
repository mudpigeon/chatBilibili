package top.nino.core.http;

import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.http.HttpHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : nino
 * @date : 2024/2/5 21:05
 */
public class HttpHeaderConstructUtil {

    public static Map<String, String> constructHeader(String cookieValue) {
        Map<String, String> headers = new HashMap<>(3);
        // 放入请求头
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);
        if (StringUtils.isNotBlank(cookieValue)) {
            // 有cookie就放
            headers.put("cookie", cookieValue);
        }
        return headers;
    }

    public static Map<String, String> constructHeader(String cookieValue, String referer) {
        Map<String, String> headers = new HashMap<>(3);
        // 放入请求头
        headers.put(HttpHeader.USER_AGENT_KEY, HttpHeader.USER_AGENT_VALUE);
        if (StringUtils.isNotBlank(cookieValue)) {
            // 有cookie就放
            headers.put(HttpHeader.COOKIE_KEY, cookieValue);
        }
        headers.put(HttpHeader.REFER_KEY, referer);
        return headers;
    }
}
