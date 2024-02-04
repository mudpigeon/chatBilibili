package top.nino.api.model.welcome;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class WelcomeGuard implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2312238019705486880L;
	//用户uid
	private Long uid;
	//用户名称
	private String username;
	//舰长等级 0 1总督 2提督 3舰长
	private Short guard_level;
	//未知
	private Short mock_effect;
}
