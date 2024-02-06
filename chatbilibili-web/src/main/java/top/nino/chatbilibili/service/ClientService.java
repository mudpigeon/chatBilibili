package top.nino.chatbilibili.service;


/**
 * @author nino
 */
public interface ClientService {
	void startConnService(Long roomId) throws Exception;
	void reConnService() throws Exception;
	boolean closeConnService();
}
