package top.nino.api.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class UserMedal implements Serializable {
    private String medalName;
    private Integer level;
    private String target_name;
    private Long target_id;
    private String target_face;
    private Long roomid;
    private Long score;
    private Long dayLimit;
    private Long day_limit;
    private Long todayFeed;
    private Long today_feed;
    private Long today_intimacy;
    private Long uid;
    private String uname;

}
