package top.nino.api.model.room;

import lombok.Data;
import top.nino.api.model.danmu.RoomInfo;


/**
 * @author nino
 */
@Data
public class RoomAllInfo {
    private RoomInfo roomInfo;
    private AnchorMedalInfo anchorMedalInfo;
}
