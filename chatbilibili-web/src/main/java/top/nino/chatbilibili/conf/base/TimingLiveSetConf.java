package top.nino.chatbilibili.conf.base;

import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public abstract class TimingLiveSetConf extends LiveSetConf{

    private double time=0;
}
