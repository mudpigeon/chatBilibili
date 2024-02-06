package top.nino.api.model.danmu;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


/**
 * @author nino
 */
@Slf4j
@NoArgsConstructor
@Data
public class DanmuUserRoleInfo implements Serializable, Cloneable {

    private static DanmuUserRoleInfo danmuUserRoleInfo = new DanmuUserRoleInfo();

    private static final long serialVersionUID = -699907643016533390L;

    // 用户uid() 位置info[2][0]
    private Long uid;

    // 用户名称() 位置info[2][1]
    private String uname;

    // 弹幕 ()位置info[1]
    private String msg;

    // 弹幕发送时间 ()位置info[0][4]
    private Long timestamp;

    // 是否为房管( 0否 1是)位置 info[2][2] 0也是可以是主播 注意 2 时主播
    private Short manager;

    // 是否为老爷 (0否 1是)位置 info[2][3]
    private Short vip;

    // 是否为年费老爷 (0否 1是)位置 info[2][4]
    private Short svip;

    // 勋章等级() 位置info[3][0] 或者[]
    private Short medal_level;

    // 勋章名称() 位置info[3][1] 或者[]
    private String medal_name;

    // 勋章归属主播()位置info[3][2] 或者[]
    private String medal_anchor;

    // 勋章归宿房间号()位置info[3][3] 或者[]
    private Long medal_room;

    // 用户等级位置info[4][0]
    private Short ulevel;

    // 用户本房间舰队身份(0非舰队，1总督，2提督，3舰长)位置info[7]
    private Short uguard;


    @Override
    protected Object clone() {
        try {
            return (DanmuUserRoleInfo) danmuUserRoleInfo.clone();
        } catch (Exception e) {
            log.error("克隆异常", e);
        }
        return new DanmuUserRoleInfo();
    }


    public static DanmuUserRoleInfo getDanmuUserRoleInfo(Long uid, String uname, String msg, Long timestamp, Short manager, Short vip, Short svip,
                                                         Short medal_level, String medal_name, String medal_anchor, Long medal_room, Short ulevel, Short uguard) {
        try {
            DanmuUserRoleInfo h = (DanmuUserRoleInfo) danmuUserRoleInfo.clone();
            h.setUid(uid);
            h.setUname(uname);
            h.setMsg(msg);
            h.setTimestamp(timestamp);
            h.setManager(manager);
            h.setVip(svip);
            h.setSvip(svip);
            h.setMedal_level(medal_level);
            h.setMedal_name(medal_name);
            h.setMedal_anchor(medal_anchor);
            h.setMedal_room(medal_room);
            h.setUlevel(ulevel);
            h.setUguard(uguard);
            return h;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DanmuUserRoleInfo();
    }

    public static DanmuUserRoleInfo copyHbarrage(DanmuMessage danmuMessage) {
        try {
            DanmuUserRoleInfo h = (DanmuUserRoleInfo) danmuUserRoleInfo.clone();
            h.setUid(danmuMessage.getUid());
            h.setUname(danmuMessage.getUname());
            h.setMsg(danmuMessage.getMsg());
            h.setTimestamp(danmuMessage.getTimestamp());
            h.setManager(danmuMessage.getManager());
            h.setVip(danmuMessage.getVip());
            h.setSvip(danmuMessage.getSvip());
            h.setMedal_level(danmuMessage.getMedal_level());
            h.setMedal_name(danmuMessage.getMedal_name());
            h.setMedal_anchor(danmuMessage.getMedal_anchor());
            h.setMedal_room(danmuMessage.getMedal_room());
            h.setUlevel(danmuMessage.getUlevel());
            h.setUguard(danmuMessage.getUguard());
            return h;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DanmuUserRoleInfo();
    }


}
