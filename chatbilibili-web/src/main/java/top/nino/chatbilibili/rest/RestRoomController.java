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
        if (ObjectUtils.isEmpty(GlobalSettingConf.webSocketProxy) || !GlobalSettingConf.webSocketProxy.isOpen()) {
            try {
                clientService.startConnService(roomId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ObjectUtils.isNotEmpty(GlobalSettingConf.ROOM_ID)) {
                GlobalSettingConf.centerSetConf.setRoomId(GlobalSettingConf.ROOM_ID);
                GlobalSettingConf.ROOMID_LONG = GlobalSettingConf.ROOM_ID;
            }
            settingService.connectSet();
        }

        return Response.success(ObjectUtils.isNotEmpty(GlobalSettingConf.webSocketProxy) && GlobalSettingConf.webSocketProxy.isOpen(), req);
    }
}
