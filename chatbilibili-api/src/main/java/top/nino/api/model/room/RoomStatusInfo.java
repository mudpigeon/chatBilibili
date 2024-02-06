package top.nino.api.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class RoomStatusInfo {

	// 直播间房间号
	private Long room_id;

	// 直播间房间短号
	private Long short_id;

	// 直播间主播uid
	private Long uid;

	// 0为不直播  1为直播中 2为轮播
	private Integer live_status;

	private Short need_p2p;

	private Boolean is_hidden;

	private Boolean is_portrait;

	private Short hidden_till;

	private Short lock_till;

	private Boolean encrypted;

	private Boolean pwd_verified;

	private Long live_time;

	private Short room_shield;

	private Short is_sp;

	private Short special_type;
}
