package top.nino.chatbilibili.thread;

import lombok.Getter;
import lombok.Setter;
import top.nino.chatbilibili.GlobalSettingConf;


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
        if(GlobalSettingConf.webSocketProxy!=null&&!GlobalSettingConf.webSocketProxy.isOpen()) {
            return;
        }
        GlobalSettingConf.ISSHIELDWELCOME = true;
        try {
            Thread.sleep(time * 1000);
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
//			e.printStackTrace();
        }
        GlobalSettingConf.ISSHIELDWELCOME = false;
    }
}
