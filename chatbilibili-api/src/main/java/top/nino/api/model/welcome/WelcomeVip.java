package top.nino.api.model.welcome;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class WelcomeVip implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -823797545698723949L;
	//用户uid
	private Long uid;
	//用户名称
	private String uname;
	//是否为管理员
	private Boolean is_admin;
	//年费老爷
	private Short svip;
	//老爷
	private Short vip;
	//未知
	private Short mock_effect;

}
