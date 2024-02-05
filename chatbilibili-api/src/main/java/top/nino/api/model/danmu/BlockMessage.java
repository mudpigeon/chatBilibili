package top.nino.api.model.danmu;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class BlockMessage implements Serializable{

	private static final long serialVersionUID = 1958790578528421482L;
	// 用户uid
	private Long uid;
	// 用户名称
	private String uname;
	// 谁封禁的  1房管 2主播
	private Short operator;

}
