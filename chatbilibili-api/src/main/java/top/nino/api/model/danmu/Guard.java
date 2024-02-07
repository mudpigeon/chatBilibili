package top.nino.api.model.danmu;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class Guard implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2253756094203399238L;
	//用户uid
	private Long uid;
	//用户名称
	private String username;
	//舰队类型 1总督 2提督 3舰长
	private Short guard_level;
	//数量
	private Integer num;
	//价格
	private Integer price;
	//角色名称？？？？ 直译 随机数吧
	private Long role_name;
	//礼物名称
	private String gift_name;
	//开始时间
	private Long start_time;
	//结束时间
	private Long end_time;
}
