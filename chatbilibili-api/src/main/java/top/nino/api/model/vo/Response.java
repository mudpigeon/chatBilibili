package top.nino.api.model.vo;

import lombok.Data;
import top.nino.api.model.enums.ResponseCode;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

;


/**
 * @author nino
 */
@Data
public class Response<T> {
	private String code;
	private String msg;
	private Object result;
	private Timestamp timestamp;

	public Response() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String times = dateFormat.format(calendar.getTime());
		Timestamp timestamp = Timestamp.valueOf(times.toString());
		this.timestamp = timestamp;
	}

	public Response(ResponseCode code, Object result, Timestamp timestamp) {
		super();
		this.code = code.getCode();
		this.msg = code.getCnMsg();
		this.result = result;
		this.timestamp=timestamp;
	}
	public Response(ResponseCode code, String msg, Object result,Timestamp timestamp) {
		super();
		this.code = code.getCode();
		this.msg = code.getCnMsg();
		this.result = result;
		this.timestamp=timestamp;
	}

	@SuppressWarnings("rawtypes")
	public static Response success(Object result, HttpServletRequest request) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String times = dateFormat.format(calendar.getTime());
		Timestamp timestamp = Timestamp.valueOf(times.toString());
		return new Response(ResponseCode.normal,"",result,timestamp);
	}

	@SuppressWarnings("rawtypes")
	public static Response error(HttpServletRequest request) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String times = dateFormat.format(calendar.getTime());
		Timestamp timestamp = Timestamp.valueOf(times.toString());
		return new Response(ResponseCode.syserror,"",timestamp);
	}

	public static Response error(ResponseCode responseCode, HttpServletRequest request) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String times = dateFormat.format(calendar.getTime());
		Timestamp timestamp = Timestamp.valueOf(times.toString());
		return new Response(responseCode,"",timestamp);
	}

}
