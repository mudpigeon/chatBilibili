package top.nino.api.model.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckTx implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6849509148214688485L;
	private Long room_id;
	private String gift_name;
	private Short time;



}
