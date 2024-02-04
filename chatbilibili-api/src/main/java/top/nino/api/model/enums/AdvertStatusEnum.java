package top.nino.api.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author cengzhongjie
 */

@AllArgsConstructor
@Getter
public enum AdvertStatusEnum {

	//顺序
	deafult(0),
	//随机
	random(1);

	private int code;

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
}
