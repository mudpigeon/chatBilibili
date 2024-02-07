package top.nino.api.model.vo;

import lombok.Data;
import top.nino.api.model.tools.FastJsonUtils;


import java.io.Serializable;

/**
 * @author nino
 */
@Data
public class WebsocketMessagePackage implements Serializable,Cloneable{
	
	private static final long serialVersionUID = 4807973278850564054L;
	private static WebsocketMessagePackage websocketMessagePackage = new WebsocketMessagePackage();
	private String cmd;
	private Short status;
	private Object result;
	

	public static WebsocketMessagePackage getWebsocketMessagePackage() {
		try {
			return (WebsocketMessagePackage) websocketMessagePackage.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new WebsocketMessagePackage();
	}

	public static WebsocketMessagePackage getWebsocketMessagePackage(String cmd, Short status, Object result) {
		try {
			WebsocketMessagePackage ws = (WebsocketMessagePackage) websocketMessagePackage.clone();
			ws.setCmd(cmd);
			ws.setStatus(status);
			ws.setResult(result);
			return ws;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new WebsocketMessagePackage();
	}

	public static String toJson(String cmd,Short status,Object result) {
		try {
			WebsocketMessagePackage ws = (WebsocketMessagePackage) websocketMessagePackage.clone();
			ws.setCmd(cmd);
			ws.setStatus(status);
			ws.setResult(result);
			return FastJsonUtils.toJson(ws);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
