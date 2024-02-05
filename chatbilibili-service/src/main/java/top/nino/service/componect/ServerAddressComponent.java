package top.nino.service.componect;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author nino
 */
@Getter
@Component
public class ServerAddressComponent{

	@Value("${server.port}")
	private int serverPort;

	public String getIpAddressUrl() {
		InetAddress address = null;
		String addressStr = "";
		try {
			address = InetAddress.getLocalHost();
			addressStr = address.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			addressStr = "获取失败";
		}
		return "http://"+ addressStr +":"+this.serverPort;
	}

	/**
	 *
	 * @return http://localhost:port
	 */
	public String getLocalDomainUrl() {
		return "http://localhost:"  + this.serverPort;
	}

}
