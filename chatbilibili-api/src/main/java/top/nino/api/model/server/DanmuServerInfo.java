package top.nino.api.model.server;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * B站弹幕服务器地址
 * @author nino
 */
@Data
@NoArgsConstructor
public class DanmuServerInfo implements Serializable{

	private static final long serialVersionUID = 8555167206959414211L;
	private String host;
	private Integer port;
	private Integer ws_port;
	private Integer wss_port;

}
