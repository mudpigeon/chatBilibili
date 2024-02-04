package top.nino.api.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor

public class AutoSendGift implements Serializable {
    private static final long serialVersionUID = 1247970966228534932L;
    private Integer id;
    private String name;
    private Integer feed;
    private Integer num;
    private Short coin_type;

    public AutoSendGift(Integer id, String name, Integer feed, Short coin_type) {
        this.id = id;
        this.name = name;
        this.feed=feed;
        this.coin_type = coin_type;
    }

}
