package top.nino.chatbilibili.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.api.model.room.LotteryInfoWeb;
import top.nino.api.model.room.Room;
import top.nino.api.model.room.RoomInfoAnchor;
import top.nino.api.model.room.RoomInit;
import top.nino.api.model.server.Conf;
import top.nino.api.model.welcome.BarrageHeadHandle;
import top.nino.api.model.welcome.FristSecurityData;
import top.nino.chatbilibili.PublicDataConf;
import top.nino.chatbilibili.client.WebSocketProxy;
import top.nino.chatbilibili.component.ThreadComponent;
import top.nino.chatbilibili.http.HttpRoomData;
import top.nino.chatbilibili.http.HttpUserData;
import top.nino.chatbilibili.service.ClientService;
import top.nino.chatbilibili.service.SetService;
import top.nino.chatbilibili.ws.HandleWebsocketPackage;
import top.nino.core.ByteUtils;
import top.nino.chatbilibili.tool.CurrencyTools;
import top.nino.chatbilibili.tool.GuardFileTools;
import top.nino.core.HexUtils;


import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


@Service
public class ClientServiceImpl implements ClientService {
    private SetService setService;
    private ThreadComponent threadComponent;

    public void startConnService(long roomid) throws Exception {
        if (roomid < 1) {
            return;
        }
        RoomInit roomInit = HttpRoomData.httpGetRoomInit(roomid);
        if (roomInit.getShort_id() > 0) {
            PublicDataConf.SHORTROOMID = roomInit.getShort_id();
        }
        PublicDataConf.ROOMID = roomInit.getRoom_id();
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
        PublicDataConf.MEDALINFOANCHOR = roomInfoAnchor.getMedalInfoAnchor();
        //公共信息处理
        PublicDataConf.AUID = roomInit.getUid();
        PublicDataConf.FANSNUM = HttpRoomData.httpGetFollowersNum();

        PublicDataConf.URL = CurrencyTools.GetWsUrl(conf.getHost_list());
        PublicDataConf.ANCHOR_NAME = room.getUname();
        PublicDataConf.lIVE_STATUS = roomInit.getLive_status();
        if (PublicDataConf.lIVE_STATUS == 1) {
            PublicDataConf.IS_ROOM_POPULARITY = true;
        }
        if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
            HttpUserData.httpGetUserBarrageMsg();
        }
        FristSecurityData fristSecurityData = null;
        if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
            fristSecurityData = new FristSecurityData(PublicDataConf.USER.getUid(), PublicDataConf.ROOMID,
                    conf.getToken());
        } else {
            //应付用户名称带星号问题
            fristSecurityData = new FristSecurityData(0l,PublicDataConf.ROOMID, conf.getToken());
            fristSecurityData.setBuvid(UUID.randomUUID()+"infoc");
        }
        byte[] byte_1 = HandleWebsocketPackage.BEhandle(BarrageHeadHandle.getBarrageHeadHandle(
                fristSecurityData.toJson().getBytes().length + PublicDataConf.packageHeadLength,
                PublicDataConf.packageHeadLength, PublicDataConf.packageVersion, PublicDataConf.firstPackageType,
                PublicDataConf.packageOther));
        byte[] byte_2 = fristSecurityData.toJson().getBytes();
        byte[] req = ByteUtils.byteMerger(byte_1, byte_2);
        //开启websocket 和 发送验证包和心跳包
        PublicDataConf.webSocketProxy = new WebSocketProxy(PublicDataConf.URL, room);
        PublicDataConf.webSocketProxy.send(req);
        PublicDataConf.webSocketProxy.send(HexUtils.fromHexString(PublicDataConf.heartByte));
        threadComponent.startHeartByteThread();
        setService.holdSet(PublicDataConf.centerSetConf);
        //检查红包+天选
        LotteryInfoWeb lotteryInfoWeb =null;
        //检查红包
        if((PublicDataConf.centerSetConf.getThank_gift().hasRdShield())
                ||(PublicDataConf.centerSetConf.getWelcome().hasRdShield())
                ||(PublicDataConf.centerSetConf.getFollow().hasRdShield())) {
            lotteryInfoWeb = HttpRoomData.httpGetLotteryInfoWeb();
            CurrencyTools.handleLotteryInfoWebByRedPackage(PublicDataConf.ROOMID, lotteryInfoWeb);
        }
        if(PublicDataConf.centerSetConf.getThank_gift().hasTxShield()
                ||PublicDataConf.centerSetConf.getWelcome().hasTxShield()
                ||PublicDataConf.centerSetConf.getFollow().hasTxShield()) {
            //检查天选
            if(lotteryInfoWeb==null) {
                lotteryInfoWeb = HttpRoomData.httpGetLotteryInfoWeb();
            }
            CurrencyTools.handleLotteryInfoWebByTx(PublicDataConf.ROOMID, lotteryInfoWeb);
        }

//        //检查天选
//        CheckTx checkTx = null;
//        // 登录发现天选屏蔽礼物
//        if (PublicDataConf.centerSetConf != null && PublicDataConf.centerSetConf.getThank_gift().is_tx_shield()) {
//            if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
//               checkTx = HttpRoomData.httpGetCheckTX();
//                if (checkTx != null) {
//                    if (StringUtils.isNotBlank(checkTx.getGift_name())) {
//                        if (checkTx.getTime() > 0) {
//                            threadComponent.startGiftShieldThread(checkTx.getGift_name(), checkTx.getTime());
//                        }
//                    }
//                }
//            }
//        }
//
//        // 登录发现天选屏蔽关注
//        if (PublicDataConf.centerSetConf != null && PublicDataConf.centerSetConf.getFollow().is_tx_shield()) {
//            if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
//                if(checkTx==null) {
//                    checkTx = HttpRoomData.httpGetCheckTX();
//                }
//                if (checkTx != null) {
//                    if (checkTx.getTime() > 0) {
//                        // do something
//                        threadComponent.startFollowShieldThread(checkTx.getTime());
//                    }
//                }
//            }
//        }
//        // 登录发现天选屏蔽欢迎
//        if (PublicDataConf.centerSetConf != null && PublicDataConf.centerSetConf.getWelcome().is_tx_shield()) {
//            if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
//                if(checkTx==null) {
//                   checkTx = HttpRoomData.httpGetCheckTX();
//                }
//                if (checkTx != null) {
//                    if (checkTx.getTime() > 0) {
//                        // do something
//                        threadComponent.startWelcomeShieldThread(checkTx.getTime());
//                    }
//                }
//            }
//        }
        //舰长本地存储处理
        if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
            if (PublicDataConf.centerSetConf != null
                    && PublicDataConf.centerSetConf.getThank_gift().is_guard_local()) {
                if (GuardFileTools.read() != null && GuardFileTools.read().size() > 0) {
                } else {
                    Map<Long, String> guards = HttpRoomData.httpGetGuardList();
                    if (guards != null && guards.size() > 0) {
                        for (Entry<Long, String> entry : guards.entrySet()) {
                            GuardFileTools.write(entry.getKey() + "," + entry.getValue());
                        }
                    }
                    if (guards != null) {
                        guards.clear();
                    }
                }
            }
        }
    }

    public void reConnService() throws Exception {
        if (!PublicDataConf.webSocketProxy.isOpen()) {
            threadComponent.closeAll();
            RoomInit roomInit = HttpRoomData.httpGetRoomInit(PublicDataConf.ROOMID);
            Room room = HttpRoomData.httpGetRoomData(PublicDataConf.ROOMID);
            try {
                if (roomInit.getRoom_id() < 1 || roomInit.getRoom_id() == null) {
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
                return;
            }
            if (roomInit.getShort_id() > 0) {
                PublicDataConf.SHORTROOMID = roomInit.getShort_id();
            }
            PublicDataConf.ROOMID = roomInit.getRoom_id();
            Conf conf = HttpRoomData.httpGetConf();
            if (conf == null) {
                return;
            }
            PublicDataConf.AUID = roomInit.getUid();
            PublicDataConf.FANSNUM = HttpRoomData.httpGetFollowersNum();

            PublicDataConf.URL = CurrencyTools.GetWsUrl(conf.getHost_list());

            PublicDataConf.ANCHOR_NAME = room.getUname();
            PublicDataConf.lIVE_STATUS = roomInit.getLive_status();
            if (PublicDataConf.lIVE_STATUS == 1) {
                PublicDataConf.IS_ROOM_POPULARITY = true;
            }
            if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
                HttpUserData.httpGetUserBarrageMsg();
            }
            FristSecurityData fristSecurityData = null;
            PublicDataConf.webSocketProxy = new WebSocketProxy(PublicDataConf.URL, room);
            if (StringUtils.isNotBlank(PublicDataConf.USERCOOKIE)) {
                fristSecurityData = new FristSecurityData(PublicDataConf.USER.getUid(), PublicDataConf.ROOMID,
                        conf.getToken());
            } else {
                fristSecurityData = new FristSecurityData(PublicDataConf.ROOMID, conf.getToken());
            }
            byte[] byte_1 = HandleWebsocketPackage.BEhandle(BarrageHeadHandle.getBarrageHeadHandle(
                    fristSecurityData.toJson().toString().getBytes().length + PublicDataConf.packageHeadLength,
                    PublicDataConf.packageHeadLength, PublicDataConf.packageVersion, PublicDataConf.firstPackageType,
                    PublicDataConf.packageOther));
            byte[] byte_2 = fristSecurityData.toJson().getBytes();
            byte[] req = ByteUtils.byteMerger(byte_1, byte_2);
            PublicDataConf.webSocketProxy.send(req);
            PublicDataConf.webSocketProxy.send(HexUtils.fromHexString(PublicDataConf.heartByte));
            threadComponent.startHeartByteThread();
            if (PublicDataConf.webSocketProxy.isOpen()) {
                setService.holdSet(PublicDataConf.centerSetConf);
            }
        }
    }

    public boolean closeConnService() {
        boolean flag = false;
        if (PublicDataConf.webSocketProxy != null) {
            if (PublicDataConf.webSocketProxy.isOpen()) {
                synchronized (PublicDataConf.webSocketProxy) {
                    PublicDataConf.webSocketProxy.close();
                    try {
                        PublicDataConf.webSocketProxy.closeBlocking();
                    } catch (InterruptedException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                    PublicDataConf.webSocketProxy.closeConnection(1000, "手动关闭");
                    PublicDataConf.webSocketProxy = null;
                }
                threadComponent.closeAll();
                PublicDataConf.init_connect();
                if (null == PublicDataConf.webSocketProxy || !PublicDataConf.webSocketProxy.isOpen()) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Autowired
    public void setSetService(SetService setService) {
        this.setService = setService;
    }
    @Autowired
    public void setThreadComponent(ThreadComponent threadComponent) {
        this.threadComponent = threadComponent;
    }
}
