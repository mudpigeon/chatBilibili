package top.nino.api.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class UserCookie implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8084610790722110108L;
	private String DedeUserID;
	private String bili_jct;
	private String DedeUserID__ckMd5;
	private String sid;
	private String SESSDATA;
	
}
