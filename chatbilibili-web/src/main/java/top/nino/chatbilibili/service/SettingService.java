package top.nino.chatbilibili.service;


import top.nino.chatbilibili.conf.base.AllSettingConfig;

/**
 * @author nino
 */
public interface SettingService {
	void changeSet(AllSettingConfig allSettingConfig);
	void changeSet(AllSettingConfig allSettingConfig, boolean check);
	void connectSet();
	void clearLoginCache();
}
