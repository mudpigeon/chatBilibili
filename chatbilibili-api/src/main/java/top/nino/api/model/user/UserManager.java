package top.nino.api.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class UserManager implements Serializable {
    private static final long serialVersionUID = -6419840093508270008L;
    private Long roomid;
    private Long short_roomid;
    private boolean is_manager;

}
