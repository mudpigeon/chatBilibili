package top.nino.api.model.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class LoginData implements Serializable{

	private static final long serialVersionUID = -1326824790653747543L;
	private String oauthKey;
	private String gourl = "https://www.bilibili.com/";
}
