package top.nino.api.model.superchat;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class GiftChat implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1794734764847323793L;
	private Integer num;
	private Integer gift_id;
	private String gift_name;

}
