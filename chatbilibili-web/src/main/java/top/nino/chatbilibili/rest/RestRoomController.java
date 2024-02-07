package top.nino.chatbilibili.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.nino.api.model.vo.Response;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SettingService;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author：nino
 * @Date：2024/2/6 14:39
 */
@Slf4j
@RestController
@RequestMapping("/rest/room")
public class RestRoomController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private SettingService settingService;

    @ResponseBody
    @GetMapping(value = "/connectRoom")
    public Response<?> connectRoom(HttpServletRequest req, @RequestParam("roomId") Long roomId) {
        if (ObjectUtils.isEmpty(GlobalSettingConf.bilibiliWebSocketProxy) || !GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
            try {
                clientService.loadRoomInfoAndOpenWebSocket(roomId);
            } catch (Exception e) {
                log.error("加载直播间信息并开启websocket异常", e);
            }
            if (ObjectUtils.isNotEmpty(GlobalSettingConf.ROOM_ID)) {
                GlobalSettingConf.ALL_SETTING_CONF.setRoomId(GlobalSettingConf.ROOM_ID);
                GlobalSettingConf.ROOMID_LONG = GlobalSettingConf.ROOM_ID;
            }
            settingService.writeAndReadSettingAndStartReceive();
        }

        return Response.success(ObjectUtils.isNotEmpty(GlobalSettingConf.bilibiliWebSocketProxy) && GlobalSettingConf.bilibiliWebSocketProxy.isOpen(), req);
    }
}
