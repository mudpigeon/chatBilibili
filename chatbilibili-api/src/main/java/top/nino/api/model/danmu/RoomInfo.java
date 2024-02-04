package top.nino.api.model.danmu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 782231567104445143L;
	private Long uid;
	private Integer short_id;
	private Long room_id;
	private String title;
	private Short live_status;
	private Long live_start_time;
	private Short lock_status;
	private Long lock_time;
	private Integer area_id;
	private Integer parent_area_id;
	private String parent_area_name;
	private String area_name;

	
	
}
