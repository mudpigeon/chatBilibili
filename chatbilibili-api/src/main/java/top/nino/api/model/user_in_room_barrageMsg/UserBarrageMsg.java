package top.nino.api.model.user_in_room_barrageMsg;


import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class UserBarrageMsg {
	private Short bubble;
	private UserBarrage danmu;
	private String uname_color;
}
