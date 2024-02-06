package top.nino.chatbilibili.conf.set;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.component.ThreadComponent;
import top.nino.chatbilibili.conf.base.StartThreadInterface;
import top.nino.chatbilibili.conf.base.TimingLiveSetConf;


import java.io.Serializable;


/**
 * @author nino
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvertSetConf extends TimingLiveSetConf implements Serializable, StartThreadInterface {

	/**
	 *
	 */
	private static final long serialVersionUID = -643702235901579872L;
	//如何发送 0 1
	private short status=0;
	//发送语
	private String adverts;



	//方法区
	@Override
	public void start(ThreadComponent threadComponent){
		if(StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)){
			return;
		}
		if (is_live_open()) {
			if (GlobalSettingConf.LIVE_STATUS != 1) {
				threadComponent.closeAdvertThread();
			} else {
				if (is_open()) {
					threadComponent.startAdvertThread(this);
				} else {
					threadComponent.closeAdvertThread();
				}
			}
		} else {
			if (is_open()) {
				threadComponent.startAdvertThread(this);
			} else {
				threadComponent.closeAdvertThread();
			}
		}
	}


}
