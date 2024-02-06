package top.nino.api.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInit {
	private Long room_id;
	private Long short_id;
	private Long uid;
	private Short need_p2p;
	private Boolean is_hidden;
	private Boolean is_portrait;
	// 0为不直播  1为直播中 2为轮播
	private Integer live_status;
	private Short hidden_till;
	private Short lock_till;
	private Boolean encrypted;
	private Boolean pwd_verified;
	private Long live_time;
	private Short room_shield;
	private Short is_sp;
	private Short special_type;


}
