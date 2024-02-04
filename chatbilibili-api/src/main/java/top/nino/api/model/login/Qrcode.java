package top.nino.api.model.login;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class Qrcode implements Serializable{

	private static final long serialVersionUID = -8700211079867769292L;
	private String url;
	private String oauthKey;
	private String qrcode_key;
}
