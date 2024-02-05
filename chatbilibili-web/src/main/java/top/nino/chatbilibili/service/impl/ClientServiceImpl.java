package top.nino.chatbilibili.service.impl;

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


import java.util.UUID;


@Service
public class ClientServiceImpl implements ClientService {
    private SettingService settingService;
    private ThreadComponent threadComponent;

    public void startConnService(long roomid) throws Exception {
        if (roomid < 1) {
            return;
        }
        RoomInit roomInit = HttpRoomData.httpGetRoomInit(roomid);
        if (roomInit.getShort_id() > 0) {
            GlobalSettingConf.SHORTROOMID = roomInit.getShort_id();
        }
        GlobalSettingConf.ROOMID = roomInit.getRoom_id();
        Room room = HttpRoomData.httpGetRoomData(roomid);
        try {
            if (roomInit.getRoom_id() < 1 || roomInit.getRoom_id() == null) {
                return;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return;
        }
        Conf conf = HttpRoomData.httpGetConf();
        if (conf == null) {
            return;
        }
        //房间详细信息获取 目前仅处理勋章
        RoomInfoAnchor roomInfoAnchor = HttpRoomData.httpGetRoomInfo();
        GlobalSettingConf.MEDALINFOANCHOR = roomInfoAnchor.getMedalInfoAnchor();
        //公共信息处理
        GlobalSettingConf.AUID = roomInit.getUid();
        GlobalSettingConf.FANSNUM = HttpRoomData.httpGetFollowersNum();

        GlobalSettingConf.URL = CurrencyTools.GetWsUrl(conf.getHost_list());
        GlobalSettingConf.ANCHOR_NAME = room.getUname();
        GlobalSettingConf.lIVE_STATUS = roomInit.getLive_status();
        if (GlobalSettingConf.lIVE_STATUS == 1) {
            GlobalSettingConf.IS_ROOM_POPULARITY = true;
        }
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            HttpUserData.httpGetUserBarrageMsg();
        }
        FristSecurityData fristSecurityData = null;
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            fristSecurityData = new FristSecurityData(GlobalSettingConf.USER.getUid(), GlobalSettingConf.ROOMID,
                    conf.getToken());
        } else {
            //应付用户名称带星号问题
            fristSecurityData = new FristSecurityData(0l, GlobalSettingConf.ROOMID, conf.getToken());
            fristSecurityData.setBuvid(UUID.randomUUID()+"infoc");
        }
        byte[] byte_1 = HandleWebsocketPackage.BEhandle(BarrageHeadHandle.getBarrageHeadHandle(
                fristSecurityData.toJson().getBytes().length + GlobalSettingConf.packageHeadLength,
                GlobalSettingConf.packageHeadLength, GlobalSettingConf.packageVersion, GlobalSettingConf.firstPackageType,
                GlobalSettingConf.packageOther));
        byte[] byte_2 = fristSecurityData.toJson().getBytes();
        byte[] req = ByteUtils.byteMerger(byte_1, byte_2);
        //开启websocket 和 发送验证包和心跳包
        GlobalSettingConf.webSocketProxy = new WebSocketProxy(GlobalSettingConf.URL, room);
        GlobalSettingConf.webSocketProxy.send(req);
        GlobalSettingConf.webSocketProxy.send(HexUtils.fromHexString(GlobalSettingConf.heartByte));
        threadComponent.startHeartByteThread();
        settingService.holdSet(GlobalSettingConf.centerSetConf);



        //舰长本地存储处理
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {

        }
    }

    public void reConnService() throws Exception {
        if (!GlobalSettingConf.webSocketProxy.isOpen()) {
            threadComponent.closeAll();
            RoomInit roomInit = HttpRoomData.httpGetRoomInit(GlobalSettingConf.ROOMID);
            Room room = HttpRoomData.httpGetRoomData(GlobalSettingConf.ROOMID);
            try {
                if (roomInit.getRoom_id() < 1 || roomInit.getRoom_id() == null) {
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                return;
            }
            if (roomInit.getShort_id() > 0) {
                GlobalSettingConf.SHORTROOMID = roomInit.getShort_id();
            }
            GlobalSettingConf.ROOMID = roomInit.getRoom_id();
            Conf conf = HttpRoomData.httpGetConf();
            if (conf == null) {
                return;
            }
            GlobalSettingConf.AUID = roomInit.getUid();
            GlobalSettingConf.FANSNUM = HttpRoomData.httpGetFollowersNum();

            GlobalSettingConf.URL = CurrencyTools.GetWsUrl(conf.getHost_list());

            GlobalSettingConf.ANCHOR_NAME = room.getUname();
            GlobalSettingConf.lIVE_STATUS = roomInit.getLive_status();
            if (GlobalSettingConf.lIVE_STATUS == 1) {
                GlobalSettingConf.IS_ROOM_POPULARITY = true;
            }
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                HttpUserData.httpGetUserBarrageMsg();
            }
            FristSecurityData fristSecurityData = null;
            GlobalSettingConf.webSocketProxy = new WebSocketProxy(GlobalSettingConf.URL, room);
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                fristSecurityData = new FristSecurityData(GlobalSettingConf.USER.getUid(), GlobalSettingConf.ROOMID,
                        conf.getToken());
            } else {
                fristSecurityData = new FristSecurityData(GlobalSettingConf.ROOMID, conf.getToken());
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
                settingService.holdSet(GlobalSettingConf.centerSetConf);
            }
        }
    }

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

    @Autowired
    public void setSetService(SettingService settingService) {
        this.settingService = settingService;
    }
    @Autowired
    public void setThreadComponent(ThreadComponent threadComponent) {
        this.threadComponent = threadComponent;
    }
}
