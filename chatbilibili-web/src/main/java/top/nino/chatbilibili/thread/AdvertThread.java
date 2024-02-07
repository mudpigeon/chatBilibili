package top.nino.chatbilibili.thread;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import top.nino.api.model.enums.AdvertStatus;
import top.nino.chatbilibili.GlobalSettingConf;


import java.math.BigDecimal;


@Getter
@Setter
public class AdvertThread extends Thread {
//	@SuppressWarnings("unused")
//	private Logger LOGGER = LogManager.getLogger(AdvertThread.class);
	public volatile boolean FLAG = false;
	private double time =0;
	private String advertBarrage;
	private AdvertStatus advertStatus;

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		super.run();
		String strings[] = null;
		while (!FLAG) {
			if (FLAG) {
				return;
			}
			if(GlobalSettingConf.bilibiliWebSocketProxy !=null&&!GlobalSettingConf.bilibiliWebSocketProxy.isOpen()) {
				return;
			}
			long delay_time = new BigDecimal(getTime()).multiply(new BigDecimal("1000")).longValue();
			if (StringUtils.indexOf(getAdvertBarrage(), "\n") != -1) {
				strings = StringUtils.split(getAdvertBarrage(), "\n");

				if (getAdvertStatus().getCode() == 0) {
					// 顺序发
					for (String string : strings) {
						try {
							Thread.sleep(delay_time);
						} catch (InterruptedException e) {
							// TODO 自动生成的 catch 块
//							LOGGER.info("广告姬线程关闭:" + e);
						}
						if (GlobalSettingConf.sendBarrageThread != null&&!GlobalSettingConf.sendBarrageThread.FLAG) {
						GlobalSettingConf.barrageString.add(string);
						synchronized (GlobalSettingConf.sendBarrageThread) {
							GlobalSettingConf.sendBarrageThread.notify();
						}
						}
					}
				} else {
					// 随机发
					try {
						Thread.sleep(delay_time);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
//						LOGGER.info("广告姬线程关闭:" + e);
					}
					int strLength = strings.length;
					if (strLength > 1) {
						int randomNum = (int) Math.ceil(Math.random() * strLength);
						if (GlobalSettingConf.sendBarrageThread != null&&!GlobalSettingConf.sendBarrageThread.FLAG) {
						GlobalSettingConf.barrageString.add(strings[randomNum - 1]);
							synchronized (GlobalSettingConf.sendBarrageThread) {
								GlobalSettingConf.sendBarrageThread.notify();
							}
						}
					} else {
						if (GlobalSettingConf.sendBarrageThread != null&&!GlobalSettingConf.sendBarrageThread.FLAG) {
						GlobalSettingConf.barrageString.add(getAdvertBarrage());
						synchronized (GlobalSettingConf.sendBarrageThread) {
							GlobalSettingConf.sendBarrageThread.notify();
						}
						}
					}

				}

			} else {
				try {
					Thread.sleep(delay_time);
				} catch (InterruptedException e) {
					// TODO 自动生成的 catch 块
//					LOGGER.info("广告姬线程关闭:" + e);
				}
				if (GlobalSettingConf.sendBarrageThread != null&&!GlobalSettingConf.sendBarrageThread.FLAG) {
				GlobalSettingConf.barrageString.add(getAdvertBarrage());
				synchronized (GlobalSettingConf.sendBarrageThread) {
					GlobalSettingConf.sendBarrageThread.notify();
				}
				}

			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
//				LOGGER.info("广告姬线程关闭:" + e);
			}

		}
	}


}
