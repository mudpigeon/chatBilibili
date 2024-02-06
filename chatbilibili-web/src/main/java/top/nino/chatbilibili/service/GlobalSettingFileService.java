package top.nino.chatbilibili.service;


/**
 * @author : nino
 * @date : 2024/2/5 20:17
 */
public interface GlobalSettingFileService {

    boolean createAndValidateCookieAndLoadAndWrite();


    void startReceiveDanmuThread();

}
