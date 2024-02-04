package top.nino.api.model.user_in_room_barrageMsg;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UserBarrageMsg {
	private Short bubble;
	private UserBarrage danmu;
	private String uname_color;
}
