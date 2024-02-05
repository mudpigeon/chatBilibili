package top.nino.chatbilibili.service;


import top.nino.chatbilibili.conf.base.CenterSetConf;

/**
 * @author nino
 */
public interface SettingService {
	void changeSet(CenterSetConf centerSetConf);
	void changeSet(CenterSetConf centerSetConf,boolean check);
	void connectSet(CenterSetConf centerSetConf);
	void quit();
}
