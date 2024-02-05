package top.nino.chatbilibili.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import top.nino.api.model.auto_reply.AutoReply;
import top.nino.api.model.danmu.Gift;
import top.nino.api.model.danmu.Interact;
import top.nino.chatbilibili.GlobalSettingConf;


@Component
public class BlackParseComponent {

    private static Logger LOGGER = LogManager.getLogger(BlackParseComponent.class);
    public boolean autoReplay_parse(AutoReply autoReply) {

        return true;
    }


    public boolean interact_parse(Interact interact) {
        //全局开启
        //1欢迎 2关注
//        if (interact.getMsg_type() == 1) {
//
//        } else if (interact.getMsg_type() == 2) {
//
//        }

        return true;
    }

    public boolean gift_parse(Gift gift) {

        return true;
    }


    public <T> boolean global_parse(T t) {

        return true;
    }


    public <T> boolean parse(T t) {
        return true;
    }
}
