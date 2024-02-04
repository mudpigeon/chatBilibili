package top.nino.api.model.room;

import lombok.Data;
import top.nino.api.model.danmu.RoomInfo;


@Data
public class RoomInfoAnchor {
    private RoomInfo roomInfo;

    private MedalInfoAnchor medalInfoAnchor;
}
