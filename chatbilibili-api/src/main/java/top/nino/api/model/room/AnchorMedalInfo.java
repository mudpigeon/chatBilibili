package top.nino.api.model.room;

import lombok.Data;


/**
 * 主播勋章信息
 * @author nino
 */
@Data
public class AnchorMedalInfo {
    private Long medal_id;
    private String medal_name;
    private Long fansclub;
}
