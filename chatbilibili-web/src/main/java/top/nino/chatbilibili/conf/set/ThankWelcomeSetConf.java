package top.nino.chatbilibili.conf.set;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import top.nino.chatbilibili.GlobalSettingConf;
import top.nino.chatbilibili.conf.base.ThankLiveSetConf;


import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThankWelcomeSetConf extends ThankLiveSetConf implements Serializable {

    private static final long serialVersionUID = 3606170913548896208L;

    //人员感谢过滤 0全部 1仅勋章 2仅舰长
    @JSONField(name = "list_people_shield_status")
    private short list_people_shield_status = 0;

    private String welcomes="欢迎%uNames%的进入直播间~";



    //方法区
    public boolean is_welcomeThank(){
        if(StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)){
            return false;
        }
        if(is_live_open()) {
            //没在直播
            if(GlobalSettingConf.lIVE_STATUS !=1){
                return false;
            }else{
                if(is_open()) {
                    return true;
                }else{
                    return false;
                }
            }
        }else{
            if(is_open()) {
                return true;
            }else{
                return false;
            }
        }
    }

    public boolean is_welcomeThank(short live_status){
        if(StringUtils.isBlank(GlobalSettingConf.COOKIE_VALUE)){
            return false;
        }
        if(is_live_open()) {
            //没在直播
            if(live_status!=1){
                return false;
            }else{
                if(is_open()) {
                    return true;
                }else{
                    return false;
                }
            }
        }else{
            if(is_open()) {
                return true;
            }else{
                return false;
            }
        }
    }
}
