package top.nino.chatbilibili.front;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.http.HttpUserData;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.service.http.HttpBilibiliServer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : nino
 * @date : 2024/2/4 20:48
 */
@Slf4j
@Controller
@RequestMapping
public class ViewController {

    @Autowired
    private SettingService settingService;

    @Autowired
    private GlobalSettingFileService globalSettingFileService;

    @GetMapping(value = {"/", "/index"})
    public String index(HttpServletRequest req, Model model) {
        if(ObjectUtils.isNotEmpty(GlobalSettingConf.USER)) {
            model.addAttribute("loginUser", GlobalSettingConf.USER);
        }
        return "index";
    }

    @RequestMapping(value = "/view/loginOut")
    public String loginOut(HttpServletRequest req) {
        req.getSession().removeAttribute("loginUser");
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            HttpBilibiliServer.loginOut(GlobalSettingConf.COOKIE_VALUE);
            settingService.clearLoginCache();
            // 因为用户信息变更了，所以要重新连接直播间
            globalSettingFileService.reConnectRoom();
        }
        return "redirect:/";
    }
}
