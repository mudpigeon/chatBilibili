package top.nino.chatbilibili.service;


import top.nino.chatbilibili.conf.base.CenterSetConf;

public interface SetService {
	void init();

	void changeSet(CenterSetConf centerSetConf);
	void changeSet(CenterSetConf centerSetConf,boolean check);
	void connectSet(CenterSetConf centerSetConf);
	void holdSet(CenterSetConf centerSetConf);
	void quit();
}
