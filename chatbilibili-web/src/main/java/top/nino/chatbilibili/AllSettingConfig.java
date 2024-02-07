package top.nino.chatbilibili;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import top.nino.api.model.tools.FastJsonUtils;
import top.nino.core.data.BASE64Utils;

import java.io.IOException;
import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
@Slf4j
public class AllSettingConfig implements Serializable {

    private static final long serialVersionUID = 1162255349476806991L;

    // 房间号
    private Long roomId;

    // 是否开启弹幕
    @JSONField(name = "is_barrage")
    private boolean is_barrage = true;

    // 弹幕显示舰长和老爷图标
    @JSONField(name = "is_barrage_guard")
    private boolean is_barrage_guard = true;

    // 弹幕显示舰长和老爷图标
    @JSONField(name = "is_barrage_vip")
    private boolean is_barrage_vip = true;

    // 弹幕显示房管图标
    @JSONField(name = "is_barrage_manager")
    private boolean is_barrage_manager = true;

    // 弹幕显示勋章图标
    @JSONField(name = "is_barrage_medal")
    private boolean is_barrage_medal = false;

    // 弹幕显示用户等级图标
    @JSONField(name = "is_barrage_ul")
    private boolean is_barrage_ul = false;

    // 是否屏蔽非当前房间勋章弹幕
    @JSONField(name = "is_barrage_anchor_shield")
    private boolean is_barrage_anchor_shield = false;

    // 信息是否显示礼物消息
    @JSONField(name = "is_gift")
    private boolean is_gift = true;

    // 信息是否显示免费礼物消息
    @JSONField(name = "is_gift_free")
    private boolean is_gift_free = true;

    // 是否开启日志线程
    @JSONField(name = "is_log")
    private boolean is_log = false;

    // 是否控制台打印
    @JSONField(name = "is_cmd")
    private boolean is_cmd = true;

    // window是否自动打开设置页面 默认open
    @JSONField(name = "win_auto_openSet")
    private boolean win_auto_openSet = true;

    // 123
    private String manager_key = "202cb962ac59075b964b07152d234b70";


    public String toJson() {
        return FastJsonUtils.toJson(this);
    }

    /**
     * Let a base64 content to a CenterSetConf object.
     * @param base64 将json字符串base64编码的内容
     * @return is not null
     */
    public static AllSettingConfig of(String base64){
        Assert.notNull(base64, "Base64 must cannot be null");
        try {
            //fastjson没有完善的javadoc, 方法抛出的异常也未明确指出. 是否考虑更换较规范的库?
            // {@link JSONObject#parseObject} 方法调用的异常捕获并未完全覆盖
            return JSONObject.parseObject(new String(BASE64Utils.decode(base64)), AllSettingConfig.class);
        } catch (IOException|JSONException e) {
            // 因用户非法操作导致的,记录等级为warn;开发者使用方法不当导致的,记录等级为error.(不那么准确,但是warn级别确定是用户非法操作导致)
            // 在数据模型中的方法面向开发者,故给予error级别记录. 其实此处的异常也会因为用户非法修改profile文件内容导致异常.
            // 解析失败
            log.error(e.getMessage(), e);
            // 在异常时提供默认对象, 避免反复处理解析失败异常
            return new AllSettingConfig();
        }
    }
}
