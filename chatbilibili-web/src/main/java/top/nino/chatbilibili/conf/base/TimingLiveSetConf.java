package top.nino.chatbilibili.conf.base;

import lombok.Data;


@Data
public abstract class TimingLiveSetConf extends LiveSetConf{

    private double time=0;
}
