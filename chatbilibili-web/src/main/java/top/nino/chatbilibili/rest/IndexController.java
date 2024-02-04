package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.PublicDataConf;
import top.nino.chatbilibili.component.TaskRegisterComponent;
import top.nino.chatbilibili.http.HttpUserData;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SetService;
import top.nino.chatbilibili.service.impl.DanmujiInitService;
import top.nino.chatbilibili.tool.CurrencyTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : nino
 * @date : 2024/2/5 02:20
 */
@Slf4j
@RestController
@RequestMapping("/web")
public class IndexController {

    private SetService checkService;
    private ClientService clientService;
    @Resource
    private DanmujiInitService danmujiInitService;
    private TaskRegisterComponent taskRegisterComponent;


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
