package top.nino.chatbilibili.conf.base;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


@Data
public abstract class LiveSetConf extends OpenSetConf{
    //是否直播有效
    @JSONField(name = "is_live_open")
    private boolean is_live_open = false;
}
