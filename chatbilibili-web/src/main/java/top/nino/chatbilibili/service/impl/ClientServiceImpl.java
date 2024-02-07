package top.nino.chatbilibili.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.api.model.room.RoomAnchorInfo;
import top.nino.api.model.room.RoomAllInfo;
import top.nino.api.model.room.RoomStatusInfo;
import top.nino.api.model.server.DanmuInfo;
import top.nino.api.model.danmu.DanmuByteDataHandle;
import top.nino.api.model.welcome.FirstSecurityData;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.client.BilibiliWebSocketProxy;
import top.nino.chatbilibili.component.ThreadComponent;
import top.nino.chatbilibili.http.HttpRoomData;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.client.utils.ParseWebsocketMessageUtils;
import top.nino.core.ByteUtils;
import top.nino.core.DanmuUtils;
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
    public void loadRoomInfoAndOpenWebSocket(Long roomId) throws Exception {

        if(ObjectUtils.isEmpty(roomId) || roomId <= 0L) {
            return;
        }

        RoomStatusInfo roomStatusInfo = HttpBilibiliServer.httpGetRoomStatusInfo(roomId);
        if(ObjectUtils.isEmpty(roomStatusInfo) || ObjectUtils.isEmpty(roomStatusInfo.getRoom_id()) || roomStatusInfo.getRoom_id() < 1) {
            return;
        }
        GlobalSettingConf.ROOM_ID = roomStatusInfo.getRoom_id();
        GlobalSettingConf.ANCHOR_UID = roomStatusInfo.getUid();
        GlobalSettingConf.LIVE_STATUS = roomStatusInfo.getLive_status();
        if (GlobalSettingConf.LIVE_STATUS == 1) {
            GlobalSettingConf.IS_ROOM_POPULARITY = true;
        }
        if (roomStatusInfo.getShort_id() > 0) {
            GlobalSettingConf.SHORT_ROOM_ID = roomStatusInfo.getShort_id();
        }

        RoomAnchorInfo roomAnchorInfo = HttpBilibiliServer.httpGetRoomAnchorInfo(roomId, GlobalSettingConf.SHORT_ROOM_ID);
        GlobalSettingConf.ANCHOR_NAME = roomAnchorInfo.getUname();
        GlobalSettingConf.FANS_NUM = HttpBilibiliServer.httpGetAnchorFanSum(GlobalSettingConf.ANCHOR_UID);

        // 房间详细信息获取 目前仅处理勋章
        RoomAllInfo roomAllInfo = HttpBilibiliServer.httpGetRoomAllInfo(GlobalSettingConf.SHORT_ROOM_ID);
        GlobalSettingConf.ANCHOR_MEDAL_INFO = roomAllInfo.getAnchorMedalInfo();

        // 获取弹幕信息
        DanmuInfo roomDanmuInfo = HttpBilibiliServer.httpGetDanmuInfo(GlobalSettingConf.COOKIE_VALUE, GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID);
        if (ObjectUtils.isEmpty(roomDanmuInfo)) {
            return;
        }
        // 获取直播弹幕服务器地址
        GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL = DanmuUtils.randomGetWebsocketUrl(GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL, roomDanmuInfo.getHost_list());

        FirstSecurityData firstSecurityData;
        if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
            // 装载用户弹幕限制信息
            GlobalSettingConf.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
            GlobalSettingConf.USER_MANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);

            firstSecurityData = new FirstSecurityData(GlobalSettingConf.USER.getUid(), GlobalSettingConf.ROOM_ID, roomDanmuInfo.getToken());
        } else {
            // 应付用户名称带星号问题
            firstSecurityData = new FirstSecurityData(0L, GlobalSettingConf.ROOM_ID, roomDanmuInfo.getToken());
            firstSecurityData.setBuvid(UUID.randomUUID()+"infoc");
        }
        // 弹幕集打包后的数据
        byte[] byte_1 = ParseWebsocketMessageUtils.BEhandle(
                DanmuByteDataHandle.getBarrageHeadHandle(
                firstSecurityData.toJson().getBytes().length + GlobalSettingConf.PACKAGE_HEAD_LENGTH,
                GlobalSettingConf.PACKAGE_HEAD_LENGTH, GlobalSettingConf.PACKAGE_VERSION, GlobalSettingConf.FIRST_PACKAGE_TYPE,
                GlobalSettingConf.packageOther));
        byte[] byte_2 = firstSecurityData.toJson().getBytes();
        byte[] requestByteData = ByteUtils.byteMerger(byte_1, byte_2);

        // 开启websocket
        GlobalSettingConf.bilibiliWebSocketProxy = new BilibiliWebSocketProxy(GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL, roomAnchorInfo);

        // 发送验证包
        GlobalSettingConf.bilibiliWebSocketProxy.send(requestByteData);

        // 发送心跳包
        GlobalSettingConf.bilibiliWebSocketProxy.send(HexUtils.fromHexString(GlobalSettingConf.HEART_BYTE));

        // 启动心跳线程
        threadComponent.startHeartCheckBilibiliDanmuServerThread();
    }

    @Override
    public void reConnService() throws Exception {
        if (!GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
            threadComponent.closeAll();
            RoomStatusInfo roomStatusInfo = HttpRoomData.httpGetRoomInit(GlobalSettingConf.ROOM_ID);
            RoomAnchorInfo roomAnchorInfo = HttpRoomData.httpGetRoomData(GlobalSettingConf.ROOM_ID);
            try {
                if (roomStatusInfo.getRoom_id() < 1 || roomStatusInfo.getRoom_id() == null) {
                    return;
                }
            } catch (Exception e) {
                return;
            }
            if (roomStatusInfo.getShort_id() > 0) {
                GlobalSettingConf.SHORT_ROOM_ID = roomStatusInfo.getShort_id();
            }
            GlobalSettingConf.ROOM_ID = roomStatusInfo.getRoom_id();
            DanmuInfo roomDanmuInfo = HttpRoomData.httpGetConf();
            if (roomDanmuInfo == null) {
                return;
            }
            GlobalSettingConf.ANCHOR_UID = roomStatusInfo.getUid();
            GlobalSettingConf.FANS_NUM = HttpRoomData.httpGetFollowersNum();

            GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL = DanmuUtils.randomGetWebsocketUrl(GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL, roomDanmuInfo.getHost_list());

            GlobalSettingConf.ANCHOR_NAME = roomAnchorInfo.getUname();
            GlobalSettingConf.LIVE_STATUS = roomStatusInfo.getLive_status();
            if (GlobalSettingConf.LIVE_STATUS == 1) {
                GlobalSettingConf.IS_ROOM_POPULARITY = true;
            }
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                GlobalSettingConf.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
                GlobalSettingConf.USER_MANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingConf.ROOM_ID, GlobalSettingConf.SHORT_ROOM_ID, GlobalSettingConf.COOKIE_VALUE);
            }
            FirstSecurityData firstSecurityData = null;
            GlobalSettingConf.bilibiliWebSocketProxy = new BilibiliWebSocketProxy(GlobalSettingConf.ROOM_DANMU_WEBSOCKET_URL, roomAnchorInfo);
            if (StringUtils.isNotBlank(GlobalSettingConf.COOKIE_VALUE)) {
                firstSecurityData = new FirstSecurityData(GlobalSettingConf.USER.getUid(), GlobalSettingConf.ROOM_ID,
                        roomDanmuInfo.getToken());
            } else {
                firstSecurityData = new FirstSecurityData(GlobalSettingConf.ROOM_ID, roomDanmuInfo.getToken());
            }
            byte[] byte_1 = ParseWebsocketMessageUtils.BEhandle(DanmuByteDataHandle.getBarrageHeadHandle(
                    firstSecurityData.toJson().toString().getBytes().length + GlobalSettingConf.PACKAGE_HEAD_LENGTH,
                    GlobalSettingConf.PACKAGE_HEAD_LENGTH, GlobalSettingConf.PACKAGE_VERSION, GlobalSettingConf.FIRST_PACKAGE_TYPE,
                    GlobalSettingConf.packageOther));
            byte[] byte_2 = firstSecurityData.toJson().getBytes();
            byte[] req = ByteUtils.byteMerger(byte_1, byte_2);
            GlobalSettingConf.bilibiliWebSocketProxy.send(req);
            GlobalSettingConf.bilibiliWebSocketProxy.send(HexUtils.fromHexString(GlobalSettingConf.HEART_BYTE));
            threadComponent.startHeartCheckBilibiliDanmuServerThread();
            if (GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
                settingService.writeAndReadSettingAndStartReceive();
            }
        }
    }

    @Override
    public boolean closeConnService() {
        boolean flag = false;
        if (GlobalSettingConf.bilibiliWebSocketProxy != null) {
            if (GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
                synchronized (GlobalSettingConf.bilibiliWebSocketProxy) {
                    GlobalSettingConf.bilibiliWebSocketProxy.close();
                    try {
                        GlobalSettingConf.bilibiliWebSocketProxy.closeBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GlobalSettingConf.bilibiliWebSocketProxy.closeConnection(1000, "手动关闭");
                    GlobalSettingConf.bilibiliWebSocketProxy = null;
                }
                threadComponent.closeAll();
                GlobalSettingConf.init_connect();
                if (null == GlobalSettingConf.bilibiliWebSocketProxy || !GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }
}
