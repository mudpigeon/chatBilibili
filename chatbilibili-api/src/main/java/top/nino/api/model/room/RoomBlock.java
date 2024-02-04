package top.nino.api.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomBlock implements Serializable {
    private String admin_name;
    private Long adminid;
    private String block_end_time;
    private String ctime;
    private Long id;
    private String msg;
    private String msg_time;
    private Long roomid;
    private Short type;
    private Long uid;
    private String uname;
    private Long blockTimeStamp;
    private Long createTimeStamp;
    private Long msgTimeStamp;


    public Long getBlockTimeStamp() {
        if(StringUtils.isNotBlank(getBlock_end_time())){
            try {
                Date now = JodaTimeUtils.parse(getBlock_end_time());
                return now.getTime();
            }catch (Exception e){
            }
        }
        return blockTimeStamp;
    }

    public Long getCreateTimeStamp() {
        if(StringUtils.isNotBlank(getCtime())){
            try {
                Date now = JodaTimeUtils.parse(getCtime());
                return now.getTime();
            }catch (Exception e){
            }
        }
        return createTimeStamp;
    }

    public Long getMsgTimeStamp() {
        if(StringUtils.isNotBlank(getMsg_time())){
            try {
                Date now = JodaTimeUtils.parse(getMsg_time());
                return now.getTime();
            }catch (Exception e){
            }
        }
        return msgTimeStamp;
    }

}
