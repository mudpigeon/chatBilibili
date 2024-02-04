package top.nino.api.model.apex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PredatorResult {

    private String game_type;
    private String plate;
    private Integer foundRank;
    private Integer val;
    private String uid;
    private Long updateTimestamp;
    private Integer totalMastersAndPreds;

}
