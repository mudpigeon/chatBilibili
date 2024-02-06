package top.nino.api.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author nino
 */
@AllArgsConstructor
@Getter
public enum ResponseCode {
	SUCCESS("200", "成功", "成功"),

	NONE_QRCODE_KEY_INFO("80000", "没有QrKey", "没有QrKey"),
	QRCODE_UN_VALID_INFO("86038", "二维码失效", "二维码失效"),
	QRCODE_NO_SCAN_INFO("86101", "未扫码", "未扫码"),
	QRCODE_NO_SECOND_CHECK_INFO("86090", "已扫码,未在手机上确认登录", "已扫码,未在手机上确认登录"),
	QRCODE_UN_KNOW_INFO("89999", "未知二维码请求错误", "未知二维码请求错误"),


	normal("200", "Successful.", "操作成功"),
	syserror("400", "System is busy!", "系统繁忙"),
	accountnotexist("101", "User does not exist.", "用户不存在"),
	accountremoved("102", "User has been disabled.", "用户已停用"),
	passworderror("103", "Account or password error.", "账号或密码错误"),
	noroleforaccount("104", "User has no role.", "用户没有任何的角色"),
	propertyUsed("105", "This value has been taken.", "该值已被占用"),
	infonotmatch("106", "Information do not match", "信息不匹配"),
	choiceUserError("107", "Please select the user", "请选择用户"),
	fiveresult("108", "已经有五条记录", "已经有五条记录"),
	paramserror("301", "Requested parameter error!", "请求参数错误"),
	tokenerror("302", "No Token!", "无token"),
	tokenfail("303", "Token validation failed!", "token验证失败"),
	adminError("304", "permission restrictions!", "超级管理员权限限制"),
	resourceError("305", "No permission", "没有该功能权限"),
	invalidlink("306", "Invalid link", "无效的链接"),
	filetimeout("316", "File was invalid!", "文件已失效"),
	filenoexist("317", "File doesn't exist!", "文件不存在"),
	providerNotExist("501", "Provider does not exist.", "供应商不存在"),
	serviceNotExist("502", "Service does not exist.", "服务不存在"),
	kpiNotExist("503", "KPI does not exist.", "KPI不存在");

	private final String code;
	private final String msg;
	private final String cnMsg;
}

