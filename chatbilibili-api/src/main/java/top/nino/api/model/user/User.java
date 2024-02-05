package top.nino.api.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * B站登录成功后返回结果
 * @author nino
 */
@Data
@NoArgsConstructor
public class User implements Serializable{

	private static final long serialVersionUID = 4638128918041411710L;
	private Long uid;
	private String uname;
	private String face; // 头像地址
	private Long silver;
	private Integer gold;
	private Integer achieve;
	private Short vip;
	private Short svip;
	private Short user_level;
	private Short user_next_level;
	private Long user_Integerimacy;
	private Long user_next_Integerimacy;
	private String user_level_rank;
	private Short user_charged;
	private Integer billCoin; // 硬币数
	
	
}
