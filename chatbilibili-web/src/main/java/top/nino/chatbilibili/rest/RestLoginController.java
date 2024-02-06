package top.nino.chatbilibili.rest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.enums.ResponseCode;
import top.nino.api.model.login.QrCodeInfo;
import top.nino.api.model.user.UserCookieInfo;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.service.GlobalSettingFileService;
import top.nino.core.CookieUtils;
import top.nino.core.QrcodeUtils;
import top.nino.service.http.HttpBilibiliServer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : nino
 * @date : 2024/2/5 02:20
 */
@Slf4j
@RestController
@RequestMapping("/rest/login")
public class RestLoginController {


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
            // 弹幕长度 和管理员信息 刷新
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                GlobalSettingConf.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
                GlobalSettingConf.USERMANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
            }
        }
        return Response.success(userCookieInfo.isValidFlag(), req);
    }

    // 拿到B站官方登录二维码信息
    @ResponseBody
    @PostMapping(value = "/getQrCodeInfo")
    public Response<?> qrCodeInfo(HttpServletRequest req) {
        QrCodeInfo qrCodeInfo = HttpBilibiliServer.httpGetQrcodeInfo();
        req.getSession().setAttribute("qrcode", qrCodeInfo);
        return Response.success(qrCodeInfo, req);
    }

    // 将url生成 二维码
    @ResponseBody
    @GetMapping(value = "/generateQrCodeByUrl")
    public void qrcode(HttpServletRequest req, HttpServletResponse resp, @RequestParam("url") String url) {
        QrcodeUtils.creatRrCode(url, 140, 140, resp);
    }


    // 检查二维码扫描情况
    @ResponseBody
    @PostMapping(value = "/checkScanQrCodeStatus")
    public Response<?> loginCheck(HttpServletRequest req) {
        QrCodeInfo qrCodeInfo = (QrCodeInfo) req.getSession().getAttribute("qrcode");
        if(ObjectUtils.isEmpty(qrCodeInfo)) {
            return Response.error(ResponseCode.NONE_QRCODE_KEY_INFO, req);
        }

        Response response = HttpBilibiliServer.httpCheckQrcodeScanStatus(qrCodeInfo.getQrcode_key());
        if(response.getCode().equals(ResponseCode.SUCCESS.getCode()) && ObjectUtils.isNotEmpty(response.getResult())) {
            GlobalSettingConf.COOKIE_VALUE = String.valueOf(response.getResult());
            GlobalSettingConf.USER_COOKIE_INFO = CookieUtils.parseCookie(GlobalSettingConf.COOKIE_VALUE);
            // 弹幕长度 和管理员信息 刷新
            GlobalSettingConf.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
            GlobalSettingConf.USERMANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
            boolean loadFlag = globalSettingFileService.createAndValidateCookieAndLoadAndWrite();
            if(loadFlag) {
                // 因为新的用户信息来了，所以要重新连接直播间
                globalSettingFileService.reConnectRoom();
            }
        }
        return response;
    }
}
