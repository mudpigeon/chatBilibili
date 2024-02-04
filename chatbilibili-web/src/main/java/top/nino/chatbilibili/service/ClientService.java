package top.nino.chatbilibili.service;


public interface ClientService {
	void startConnService(long roomid) throws Exception;
	void reConnService() throws Exception;
	boolean closeConnService();
}
