package top.nino.api.model.danmu;

import lombok.Data;
import top.nino.api.model.superchat.MedalInfo;


import java.io.Serializable;


@Data
public class RedPackage implements Serializable {
    private Long lot_id;
    private Long start_time;
    private Long current_time;

    private Integer wait_num;

    private String uname;

    private Long uid;

    private String action;

    private Integer num;

    private String gift_name;

    private Long gift_id;

    private Integer price;

    private String name_color;

    private MedalInfo medal_info;
}
