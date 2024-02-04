package top.nino.chatbilibili.conf.set;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.nino.chatbilibili.conf.base.OpenSetConf;


import java.io.Serializable;


/**
 * @author nino
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoSendGiftConf extends OpenSetConf implements Serializable {
    private static final long serialVersionUID = -264415209929286293L;

    private String room_id;

    private String time = "23:45:00";

}
