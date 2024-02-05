package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.http.HttpUserData;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.chatbilibili.service.SettingService;
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


    @Resource
    private GlobalSettingFileService globalSettingFileService;


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
            boolean loadFlag = globalSettingFileService.createAndValidateCookieAndLoadAndWrite();
            if(loadFlag) {
                // 因为新的用户信息来了，所以要重新连接直播间
                globalSettingFileService.reConnectRoom();
            }
            // 弹幕长度刷新
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                HttpUserData.httpGetUserBarrageMsg();
            }
        }
        return Response.success(userCookieInfo.isValidFlag(), req);
    }
}
