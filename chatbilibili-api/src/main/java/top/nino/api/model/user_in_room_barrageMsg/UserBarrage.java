package top.nino.api.model.user_in_room_barrageMsg;

import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class UserBarrage implements Serializable{

	private static final long serialVersionUID = 985041146105605410L;
	private Short mode;
	private Long color;
	private Short length = 20;
	private Long room_id;
}
