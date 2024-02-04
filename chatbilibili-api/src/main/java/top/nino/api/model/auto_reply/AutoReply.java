package top.nino.api.model.auto_reply;

import lombok.Data;

import java.io.Serializable;


/**
 * @author nino
 */
@Data
public class AutoReply implements Serializable,Cloneable{
	/**
	 * 
	 */
	private static AutoReply autoReply = new AutoReply();
	private static final long serialVersionUID = -4026920122195895200L;
	private Long uid;
	private String name;
	private String barrage;
	
	public AutoReply() {
		super();
	}
	
	public static AutoReply getAutoReply() {
		try {
			return (AutoReply) autoReply.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return new AutoReply();
	}
	
	public static AutoReply getAutoReply(Long uid,String name,String barrage) {
		try {
			AutoReply ar = (AutoReply) autoReply.clone();
			ar.setUid(uid);
			ar.setName(name);
			ar.setBarrage(barrage);
			return ar;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return new AutoReply();
	}

	
}
