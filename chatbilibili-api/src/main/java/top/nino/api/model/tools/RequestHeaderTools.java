package top.nino.api.model.tools;

import java.util.HashMap;
import java.util.Map;


public class RequestHeaderTools {
    private RequestHeaderTools() {
    }

    public static Map<String,String> parseCookies(String cookieCluster){
        HashMap<String, String> cookieKeyValueMap = new HashMap<>();
        String cookiesString = cookieCluster.trim();
        String[] cookies = cookiesString.split(";");
        for (String cookieString : cookies) {
            if (cookieString.contains("=")) {
                String[] maps = cookieString.split("=");
                cookieKeyValueMap.put(maps[0],maps.length >= 2 ? maps[1] : "");
            }
        }

        return cookieKeyValueMap;
    }
}
