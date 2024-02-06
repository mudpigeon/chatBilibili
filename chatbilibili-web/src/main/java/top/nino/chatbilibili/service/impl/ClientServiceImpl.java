package top.nino.chatbilibili.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.api.model.room.Room;
import top.nino.api.model.room.RoomInfoAnchor;
import top.nino.api.model.room.RoomInit;
import top.nino.api.model.server.Conf;
import top.nino.api.model.welcome.BarrageHeadHandle;
import top.nino.api.model.welcome.FristSecurityData;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.client.WebSocketProxy;
import top.nino.chatbilibili.component.ThreadComponent;
import top.nino.chatbilibili.http.HttpRoomData;
import top.nino.chatbilibili.http.HttpUserData;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.ws.HandleWebsocketPackage;
import top.nino.core.ByteUtils;
import top.nino.chatbilibili.tool.CurrencyTools;
import top.nino.core.HexUtils;
import top.nino.service.http.HttpBilibiliServer;


import java.util.UUID;

/**
 * @author nino
 */
@Slf4j
@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private SettingService settingService;
    @Autowired
    private ThreadComponent threadComponent;

    @Override
    public void startConnService(Long roomId) throws Exception {

        if(ObjectUtils.isEmpty(roomId) || roomId <= 0L) {
            return;
        }

        RoomInit roomInit = HttpRoomData.httpGetRoomInit(roomId);
        if(ObjectUtils.isEmpty(roomInit) || ObjectUtils.isEmpty(roomInit.getRoom_id()) || roomInit.getRoom_id() < 1) {
            return;
        }
        GlobalSettingConf.ROOM_ID = roomInit.getRoom_id();
        GlobalSettingConf.ANCHOR_UID = roomInit.getUid();
        GlobalSettingConf.LIVE_STATUS = roomInit.getLive_status();
        if (GlobalSettingConf.LIVE_STATUS == 1) {
            GlobalSettingConf.IS_ROOM_POPULARITY = true;
        }
        if (roomInit.getShort_id() > 0) {
            GlobalSettingConf.SHORT_ROOM_ID = roomInit.getShort_id();
        }

        Room room = HttpRoomData.httpGetRoomData(roomId);
        GlobalSettingConf.ANCHOR_NAME = room.getUname();
        GlobalSettingConf.FANS_NUM = HttpRoomData.httpGetFollowersNum();

        // 房间详细信息获取 目前仅处理勋章
        RoomInfoAnchor roomInfoAnchor = HttpRoomData.httpGetRoomInfo();
        GlobalSettingConf.MEDALINFOANCHOR = roomInfoAnchor.getMedalInfoAnchor();

        Conf roomConf = HttpRoomData.httpGetConf();
        if (ObjectUtils.isEmpty(roomConf)) {
            return;
        }
        // 获取直播弹幕服务器地址
        GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL = CurrencyTools.GetWsUrl(roomConf.getHost_list());

        FristSecurityData fristSecurityData;
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {

            GlobalSettingConf.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
            GlobalSettingConf.USERMANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);

            fristSecurityData = new FristSecurityData(GlobalSettingConf.USER.getUid(), GlobalSettingConf.ROOM_ID, roomConf.getToken());
        } else {
            // 应付用户名称带星号问题
            fristSecurityData = new FristSecurityData(0l, GlobalSettingConf.ROOM_ID, roomConf.getToken());
            fristSecurityData.setBuvid(UUID.randomUUID()+"infoc");
        }
        byte[] byte_1 = HandleWebsocketPackage.BEhandle(BarrageHeadHandle.getBarrageHeadHandle(
                fristSecurityData.toJson().getBytes().length + GlobalSettingConf.packageHeadLength,
                GlobalSettingConf.packageHeadLength, GlobalSettingConf.packageVersion, GlobalSettingConf.firstPackageType,
                GlobalSettingConf.packageOther));
        byte[] byte_2 = fristSecurityData.toJson().getBytes();
        byte[] req = ByteUtils.byteMerger(byte_1, byte_2);

        // 开启websocket 和 发送验证包和心跳包
        GlobalSettingConf.webSocketProxy = new WebSocketProxy(GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL, room);
        GlobalSettingConf.webSocketProxy.send(req);
        GlobalSettingConf.webSocketProxy.send(HexUtils.fromHexString(GlobalSettingConf.heartByte));
        threadComponent.startHeartByteThread();
        settingService.connectSet();
    }

    @Override
    public void reConnService() throws Exception {
        if (!GlobalSettingConf.webSocketProxy.isOpen()) {
            threadComponent.closeAll();
            RoomInit roomInit = HttpRoomData.httpGetRoomInit(GlobalSettingConf.ROOM_ID);
            Room room = HttpRoomData.httpGetRoomData(GlobalSettingConf.ROOM_ID);
            try {
                if (roomInit.getRoom_id() < 1 || roomInit.getRoom_id() == null) {
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                return;
            }
            if (roomInit.getShort_id() > 0) {
                GlobalSettingConf.SHORT_ROOM_ID = roomInit.getShort_id();
            }
            GlobalSettingConf.ROOM_ID = roomInit.getRoom_id();
            Conf conf = HttpRoomData.httpGetConf();
            if (conf == null) {
                return;
            }
            GlobalSettingConf.ANCHOR_UID = roomInit.getUid();
            GlobalSettingConf.FANS_NUM = HttpRoomData.httpGetFollowersNum();

            GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL = CurrencyTools.GetWsUrl(conf.getHost_list());

            GlobalSettingConf.ANCHOR_NAME = room.getUname();
            GlobalSettingConf.LIVE_STATUS = roomInit.getLive_status();
            if (GlobalSettingConf.LIVE_STATUS == 1) {
                GlobalSettingConf.IS_ROOM_POPULARITY = true;
            }
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                GlobalSettingConf.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
                GlobalSettingConf.USERMANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
            }
            FristSecurityData fristSecurityData = null;
            GlobalSettingConf.webSocketProxy = new WebSocketProxy(GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL, room);
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                fristSecurityData = new FristSecurityData(GlobalSettingConf.USER.getUid(), GlobalSettingConf.ROOM_ID,
                        conf.getToken());
            } else {
                fristSecurityData = new FristSecurityData(GlobalSettingConf.ROOM_ID, conf.getToken());
            }
            byte[] byte_1 = HandleWebsocketPackage.BEhandle(BarrageHeadHandle.getBarrageHeadHandle(
                    fristSecurityData.toJson().toString().getBytes().length + GlobalSettingConf.packageHeadLength,
                    GlobalSettingConf.packageHeadLength, GlobalSettingConf.packageVersion, GlobalSettingConf.firstPackageType,
                    GlobalSettingConf.packageOther));
            byte[] byte_2 = fristSecurityData.toJson().getBytes();
            byte[] req = ByteUtils.byteMerger(byte_1, byte_2);
            GlobalSettingConf.webSocketProxy.send(req);
            GlobalSettingConf.webSocketProxy.send(HexUtils.fromHexString(GlobalSettingConf.heartByte));
            threadComponent.startHeartByteThread();
            if (GlobalSettingConf.webSocketProxy.isOpen()) {
                settingService.connectSet();
            }
        }
    }

    @Override
    public boolean closeConnService() {
        boolean flag = false;
        if (GlobalSettingConf.webSocketProxy != null) {
            if (GlobalSettingConf.webSocketProxy.isOpen()) {
                synchronized (GlobalSettingConf.webSocketProxy) {
                    GlobalSettingConf.webSocketProxy.close();
                    try {
                        GlobalSettingConf.webSocketProxy.closeBlocking();
                    } catch (InterruptedException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                    GlobalSettingConf.webSocketProxy.closeConnection(1000, "手动关闭");
                    GlobalSettingConf.webSocketProxy = null;
                }
                threadComponent.closeAll();
                GlobalSettingConf.init_connect();
                if (null == GlobalSettingConf.webSocketProxy || !GlobalSettingConf.webSocketProxy.isOpen()) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }
}
