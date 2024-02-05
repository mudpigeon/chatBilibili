package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.http.HttpUserData;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SetService;
import top.nino.chatbilibili.service.impl.DanmujiInitService;
import top.nino.core.CookieUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : nino
 * @date : 2024/2/5 02:20
 */
@Slf4j
@RestController
@RequestMapping("/rest/login")
public class WebLoginRestController {

    private SetService checkService;

    private ClientService clientService;

    @Resource
    private DanmujiInitService danmujiInitService;


    /**
     * 手动输入cookie
     * @param cookieValue
     * @param req
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/loginByCookie")
    public Response<?> loginByCookie(@RequestParam(name = "cookieValue") String cookieValue, HttpServletRequest req){
        UserCookieInfo userCookieInfo = CookieUtils.parseCookie(cookieValue);
        if(userCookieInfo.isValidFlag()){
            GlobalSettingConf.COOKIE_VALUE = cookieValue;
            GlobalSettingConf.USER_COOKIE_INFO = userCookieInfo;

            danmujiInitService.init();
            //弹幕长度刷新
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                HttpUserData.httpGetUserBarrageMsg();
            }
        }
        return Response.success(userCookieInfo.isValidFlag(), req);
    }
}
