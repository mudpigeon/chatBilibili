package top.nino.api.model.user;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 解析cookie后存储的数据
 * @author nino
 */
@Data
@NoArgsConstructor
public class UserCookieInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8084610790722110108L;
	private String DedeUserID;
	private String bili_jct;
	private String DedeUserID__ckMd5;
	private String sid;
	private String SESSDATA;

	// 我自己加的
	private boolean validFlag = false;
	
}
