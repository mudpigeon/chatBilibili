package top.nino.chatbilibili.thread;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.danmu.Interact;
import top.nino.chatbilibili.GlobalSettingConf;

import java.util.Vector;


@Getter
@Setter
public class ParseThankFollowThread extends Thread {
	//	private Logger LOGGER = LogManager.getLogger(ParseThankFollowThread.class);
	public volatile boolean FLAG = false;
	private String thankFollowString = "感谢 %uNames% 的关注";
	private Short num = 1;
	private Long delaytime = 3000L;
	private Long timestamp;
	@Override
	public void run() {
		String thankFollowStr = null;
		StringBuilder stringBuilder = new StringBuilder(300);
		Vector<Interact> interacts = new Vector<Interact>();
		synchronized (timestamp) {
			while (!FLAG) {
				if (FLAG) {
					return;
				}
				if(GlobalSettingConf.bilibiliWebSocketProxy !=null&&!GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
					return;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
				}
				long nowTime = System.currentTimeMillis();
				if (nowTime - getTimestamp() < getDelaytime()) {
				} else {

					break;
				}
			}
		}

	}

	public String handleThankStr(String thankStr) {
		String thankFollowStrs[] = null;
		if (StringUtils.indexOf(thankStr, "\n") != -1) {
			thankFollowStrs = StringUtils.split(thankStr, "\n");
		}
		if(thankFollowStrs!=null&&thankFollowStrs.length>1) {
			return thankFollowStrs[(int) Math.ceil(Math.random() * thankFollowStrs.length)-1];
		}
		return thankStr;
	}



}
