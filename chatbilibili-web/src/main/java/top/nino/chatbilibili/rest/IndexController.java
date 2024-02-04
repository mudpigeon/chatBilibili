package top.nino.chatbilibili.rest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.nino.api.model.vo.Response;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : nino
 * @date : 2024/2/5 02:20
 */
@RestController
@RequestMapping("/web")
public class IndexController {


    /**
     * 手动输入cookie
     * @param cookie
     * @param req
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loginByCookie")
    public Response<?> customCookie(String cookie, HttpServletRequest req){
        boolean flag = CurrencyTools.parseCookie(cookie);
        if(flag){
            danmujiInitService.init();
            //弹幕长度刷新
            if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
                HttpUserData.httpGetUserBarrageMsg();
            }
        }
        return Response.success(flag,req);
    }
}
