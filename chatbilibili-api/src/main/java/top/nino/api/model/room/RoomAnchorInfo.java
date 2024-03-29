package top.nino.api.model.room;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 主播和直播 关联信息
 * @author nino
 */
@Data
@NoArgsConstructor
public class RoomAnchorInfo implements Serializable {

	private static final long serialVersionUID = -8480650792452371498L;

	// 真实房间号
	private String roomid;

	// 主播uid
	private String uid;

	// 主播公告
	private String content;

	// 公告发布时间
	private String time;

	// 不知道什么状态
	private String statue;

	// 主播名称
	private String uname;

}
