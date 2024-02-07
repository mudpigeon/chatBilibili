package top.nino.api.model.danmu;

import lombok.Data;
import lombok.NoArgsConstructor;
import top.nino.api.model.superchat.MedalInfo;

import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class Interact implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8108196758728766627L;
	private Long uid;
	private String uname;
	private String uname_color;
	private Integer[] identities;
	//1欢迎 2关注
	private Short msg_type;
	private Long roomid;
	private Long timestamp;
	private Long score;

	private MedalInfo fans_medal;

	
}
