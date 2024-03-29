package top.nino.api.model.server;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * @author nino
 */
@Data
@NoArgsConstructor
public class DanmuInfo implements Serializable{

	private static final long serialVersionUID = 4877957980247956836L;

	private Short business_id;

	private String group;

	// B站弹幕服务器地址
	private List<DanmuServerInfo> host_list;

	private Short max_delay;

	private Short refresh_rate;

	private Short refresh_row_factor;

	private String token;
}
