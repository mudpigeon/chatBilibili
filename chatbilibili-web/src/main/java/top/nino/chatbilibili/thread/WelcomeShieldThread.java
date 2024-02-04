package top.nino.chatbilibili.thread;

import lombok.Getter;
import lombok.Setter;
import top.nino.chatbilibili.PublicDataConf;


@Getter
@Setter
public class WelcomeShieldThread extends Thread{
    public volatile boolean FLAG = false;
    private int time = 300;

    @Override
    public void run() {
        // TODO 自动生成的方法存根
        super.run();
        if (FLAG) {
            return;
        }
        if(PublicDataConf.webSocketProxy!=null&&!PublicDataConf.webSocketProxy.isOpen()) {
            return;
        }
        PublicDataConf.ISSHIELDWELCOME = true;
        try {
            Thread.sleep(time * 1000);
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
//			e.printStackTrace();
        }
        PublicDataConf.ISSHIELDWELCOME = false;
    }
}
