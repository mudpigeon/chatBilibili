package top.nino.api.model.conf;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public abstract class OpenSetConf {
    //是否开启
    @JSONField(name = "is_open")
    private boolean is_open = false;
}
