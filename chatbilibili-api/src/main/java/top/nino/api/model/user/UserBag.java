package top.nino.api.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBag {
    private Long bag_id;
    private Integer gift_id;
    private String gift_name;
    private Integer gift_num;
    private Integer gift_type;
    private Integer expire_at;
    private String corner_mark;
    private String corner_color;
    private List<UserBagCount> count_map;
    private Integer bind_roomid;
    private String bind_room_text;
    private Integer type;
    private String card_image;
    private String card_gif;
    private Integer card_id;
    private Integer card_record_id;
    private boolean is_show_send;
    private Integer feed;
}
