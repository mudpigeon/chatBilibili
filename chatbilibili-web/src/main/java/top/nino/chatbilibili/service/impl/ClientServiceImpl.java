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
import top.nino.api.model.danmu.FirstSecurityData;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.chatbilibili.client.BilibiliWebSocketProxy;
import top.nino.chatbilibili.service.ThreadService;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SettingService;
import top.nino.chatbilibili.client.utils.ParseWebsocketMessageUtils;
import top.nino.core.data.ByteUtils;
import top.nino.core.websocket.DanmuUtils;
import top.nino.core.data.HexUtils;
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
    private ThreadService threadService;

    @Override
    public void loadRoomInfoAndOpenWebSocket(Long roomId) throws Exception {

        if(ObjectUtils.isEmpty(roomId) || roomId <= 0L) {
            return;
        }

        RoomStatusInfo roomStatusInfo = HttpBilibiliServer.httpGetRoomStatusInfo(roomId);
        if(ObjectUtils.isEmpty(roomStatusInfo) || ObjectUtils.isEmpty(roomStatusInfo.getRoom_id()) || roomStatusInfo.getRoom_id() < 1) {
            return;
        }
        GlobalSettingCache.ROOM_ID = roomStatusInfo.getRoom_id();
        GlobalSettingCache.ANCHOR_UID = roomStatusInfo.getUid();
        GlobalSettingCache.LIVE_STATUS = roomStatusInfo.getLive_status();
        if (GlobalSettingCache.LIVE_STATUS == 1) {
            GlobalSettingCache.IS_ROOM_POPULARITY = true;
        }
        if (roomStatusInfo.getShort_id() > 0) {
            GlobalSettingCache.SHORT_ROOM_ID = roomStatusInfo.getShort_id();
        }

        RoomAnchorInfo roomAnchorInfo = HttpBilibiliServer.httpGetRoomAnchorInfo(roomId, GlobalSettingCache.SHORT_ROOM_ID);
        GlobalSettingCache.ANCHOR_NAME = roomAnchorInfo.getUname();
        GlobalSettingCache.FANS_NUM = HttpBilibiliServer.httpGetAnchorFanSum(GlobalSettingCache.ANCHOR_UID);

        // 房间详细信息获取 目前仅处理勋章
        RoomAllInfo roomAllInfo = HttpBilibiliServer.httpGetRoomAllInfo(GlobalSettingCache.SHORT_ROOM_ID);
        GlobalSettingCache.ANCHOR_MEDAL_INFO = roomAllInfo.getAnchorMedalInfo();

        // 获取弹幕信息
        DanmuInfo roomDanmuInfo = HttpBilibiliServer.httpGetDanmuInfo(GlobalSettingCache.COOKIE_VALUE, GlobalSettingCache.ROOM_ID, GlobalSettingCache.SHORT_ROOM_ID);
        if (ObjectUtils.isEmpty(roomDanmuInfo)) {
            return;
        }
        // 获取直播弹幕服务器地址
        GlobalSettingCache.ROOM_DANMU_WEBSOCKET_URL = DanmuUtils.randomGetWebsocketUrl(GlobalSettingCache.ROOM_DANMU_WEBSOCKET_URL, roomDanmuInfo.getHost_list());

        FirstSecurityData firstSecurityData;
        if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {
            // 装载用户弹幕限制信息
            GlobalSettingCache.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingCache.SHORT_ROOM_ID, GlobalSettingCache.COOKIE_VALUE);
            GlobalSettingCache.USER_MANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingCache.ROOM_ID, GlobalSettingCache.SHORT_ROOM_ID, GlobalSettingCache.COOKIE_VALUE);

            firstSecurityData = new FirstSecurityData(GlobalSettingCache.USER.getUid(), GlobalSettingCache.ROOM_ID, roomDanmuInfo.getToken());
        } else {
            // 应付用户名称带星号问题
            firstSecurityData = new FirstSecurityData(0L, GlobalSettingCache.ROOM_ID, roomDanmuInfo.getToken());
            firstSecurityData.setBuvid(UUID.randomUUID()+"infoc");
        }
        // 弹幕集打包后的数据
        byte[] byte_1 = ParseWebsocketMessageUtils.BEhandle(
                DanmuByteDataHandle.getBarrageHeadHandle(
                firstSecurityData.toJson().getBytes().length + GlobalSettingCache.PACKAGE_HEAD_LENGTH,
                GlobalSettingCache.PACKAGE_HEAD_LENGTH, GlobalSettingCache.PACKAGE_VERSION, GlobalSettingCache.FIRST_PACKAGE_TYPE,
                GlobalSettingCache.packageOther));
        byte[] byte_2 = firstSecurityData.toJson().getBytes();
        byte[] requestByteData = ByteUtils.byteMerger(byte_1, byte_2);

        // 开启websocket
        GlobalSettingCache.bilibiliWebSocketProxy = new BilibiliWebSocketProxy(GlobalSettingCache.ROOM_DANMU_WEBSOCKET_URL, roomAnchorInfo);

        // 发送验证包
        GlobalSettingCache.bilibiliWebSocketProxy.send(requestByteData);

        // 发送心跳包
        GlobalSettingCache.bilibiliWebSocketProxy.send(HexUtils.fromHexString(GlobalSettingCache.HEART_BYTE));

        // 启动心跳线程
        threadService.startHeartCheckBilibiliDanmuServerThread();
    }

    @Override
    public void reConnService() throws Exception {
        if (!GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
            threadService.closeAll();
            RoomStatusInfo roomStatusInfo = HttpBilibiliServer.httpGetRoomStatusInfo(GlobalSettingCache.ROOM_ID);
            RoomAnchorInfo roomAnchorInfo = HttpBilibiliServer.httpGetRoomAnchorInfo(GlobalSettingCache.ROOM_ID, GlobalSettingCache.SHORT_ROOM_ID);
            try {
                if (roomStatusInfo.getRoom_id() < 1 || roomStatusInfo.getRoom_id() == null) {
                    return;
                }
            } catch (Exception e) {
                return;
            }
            if (roomStatusInfo.getShort_id() > 0) {
                GlobalSettingCache.SHORT_ROOM_ID = roomStatusInfo.getShort_id();
            }
            GlobalSettingCache.ROOM_ID = roomStatusInfo.getRoom_id();
            DanmuInfo roomDanmuInfo = HttpBilibiliServer.httpGetDanmuInfo(GlobalSettingCache.COOKIE_VALUE, GlobalSettingCache.ROOM_ID, GlobalSettingCache.SHORT_ROOM_ID);
            if (roomDanmuInfo == null) {
                return;
            }
            GlobalSettingCache.ANCHOR_UID = roomStatusInfo.getUid();
            GlobalSettingCache.FANS_NUM = HttpBilibiliServer.httpGetAnchorFanSum(GlobalSettingCache.ANCHOR_UID);

            GlobalSettingCache.ROOM_DANMU_WEBSOCKET_URL = DanmuUtils.randomGetWebsocketUrl(GlobalSettingCache.ROOM_DANMU_WEBSOCKET_URL, roomDanmuInfo.getHost_list());

            GlobalSettingCache.ANCHOR_NAME = roomAnchorInfo.getUname();
            GlobalSettingCache.LIVE_STATUS = roomStatusInfo.getLive_status();
            if (GlobalSettingCache.LIVE_STATUS == 1) {
                GlobalSettingCache.IS_ROOM_POPULARITY = true;
            }
            if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {
                GlobalSettingCache.USER_BARRAGE_MESSAGE = HttpBilibiliServer.httpGetUserBarrageMsg(GlobalSettingCache.SHORT_ROOM_ID, GlobalSettingCache.COOKIE_VALUE);
                GlobalSettingCache.USER_MANAGER = HttpBilibiliServer.httpGetUserManagerMsg(GlobalSettingCache.ROOM_ID, GlobalSettingCache.SHORT_ROOM_ID, GlobalSettingCache.COOKIE_VALUE);
            }
            FirstSecurityData firstSecurityData = null;
            GlobalSettingCache.bilibiliWebSocketProxy = new BilibiliWebSocketProxy(GlobalSettingCache.ROOM_DANMU_WEBSOCKET_URL, roomAnchorInfo);
            if (StringUtils.isNotBlank(GlobalSettingCache.COOKIE_VALUE)) {
                firstSecurityData = new FirstSecurityData(GlobalSettingCache.USER.getUid(), GlobalSettingCache.ROOM_ID,
                        roomDanmuInfo.getToken());
            } else {
                firstSecurityData = new FirstSecurityData(GlobalSettingCache.ROOM_ID, roomDanmuInfo.getToken());
            }
            byte[] byte_1 = ParseWebsocketMessageUtils.BEhandle(DanmuByteDataHandle.getBarrageHeadHandle(
                    firstSecurityData.toJson().toString().getBytes().length + GlobalSettingCache.PACKAGE_HEAD_LENGTH,
                    GlobalSettingCache.PACKAGE_HEAD_LENGTH, GlobalSettingCache.PACKAGE_VERSION, GlobalSettingCache.FIRST_PACKAGE_TYPE,
                    GlobalSettingCache.packageOther));
            byte[] byte_2 = firstSecurityData.toJson().getBytes();
            byte[] req = ByteUtils.byteMerger(byte_1, byte_2);
            GlobalSettingCache.bilibiliWebSocketProxy.send(req);
            GlobalSettingCache.bilibiliWebSocketProxy.send(HexUtils.fromHexString(GlobalSettingCache.HEART_BYTE));
            threadService.startHeartCheckBilibiliDanmuServerThread();
            if (GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
                settingService.writeAndReadSettingAndStartReceive();
            }
        }
    }

    @Override
    public boolean closeConnService() {
        boolean flag = false;
        if (GlobalSettingCache.bilibiliWebSocketProxy != null) {
            if (GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
                synchronized (GlobalSettingCache.bilibiliWebSocketProxy) {
                    GlobalSettingCache.bilibiliWebSocketProxy.close();
                    try {
                        GlobalSettingCache.bilibiliWebSocketProxy.closeBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GlobalSettingCache.bilibiliWebSocketProxy.closeConnection(1000, "手动关闭");
                    GlobalSettingCache.bilibiliWebSocketProxy = null;
                }
                threadService.closeAll();
                GlobalSettingCache.init_connect();
                if (null == GlobalSettingCache.bilibiliWebSocketProxy || !GlobalSettingCache.bilibiliWebSocketProxy.isOpen()) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }
}
