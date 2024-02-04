package top.nino.chatbilibili.conf.set;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.nino.chatbilibili.conf.base.OpenSetConf;


/**
 * @author nino
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySetConf extends OpenSetConf {

    //必须得满足请求参数 和 返回参数的小心心s参数地址
    private String small_heart_url = "http://biliheart-1.herokuapp.com/enc";


    private int signDay=0;

    private int clockInDay=0;

}
