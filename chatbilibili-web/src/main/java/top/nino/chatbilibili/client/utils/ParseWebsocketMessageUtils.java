package top.nino.chatbilibili.client.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import struct.JavaStruct;
import struct.StructException;
import top.nino.api.model.danmu.DanmuByteDataHandle;
import top.nino.chatbilibili.GlobalSettingCache;
import top.nino.core.data.ByteUtils;


import java.nio.ByteBuffer;


/**
 * @author nino
 */
@Slf4j
public class ParseWebsocketMessageUtils {

	
	/**
	 * 处理父数据包 单线程引用写法
	 * 
	 * @param message
	 */
	public static void parseMessage(ByteBuffer message) throws Exception{

		byte[] messageBytes = ByteUtils.decodeValue(message);
		if(ObjectUtils.isEmpty(messageBytes) || messageBytes.length == 0) {
			return;
		}

		DanmuByteDataHandle danmuByteDataHandle = unBEhandle(messageBytes);

		int head_len = danmuByteDataHandle.getPackageHeadLength();
		int data_len = danmuByteDataHandle.getPackageLength();
		int data_ver = danmuByteDataHandle.getPackageVersion();
		int data_type = danmuByteDataHandle.getPackageType();
		int data_other = danmuByteDataHandle.getPackageOther();

		byte[] dataBodyBytes = ByteUtils.subBytes(messageBytes, head_len, data_len - head_len);


		// 弹幕信息
		if (data_ver == 0) {
			try {
				String resultStr = new String(dataBodyBytes, "utf-8");

				// 放入待处理弹幕集合
				GlobalSettingCache.danmuList.add(resultStr);

				if (ObjectUtils.isNotEmpty(GlobalSettingCache.parseDanmuMessageThread) && !GlobalSettingCache.parseDanmuMessageThread.closeFlag) {
					synchronized (GlobalSettingCache.parseDanmuMessageThread) {
						GlobalSettingCache.parseDanmuMessageThread.notify();
					}
				}

			} catch (Exception e) {
				log.info("放入待处理弹幕集合，并通知线程处理，过程异常",e );
			}
		}

		if (data_ver == 1) {

			// 房间人气
			if (data_type == 3) {
				try {
					GlobalSettingCache.ROOM_POPULARITY = ByteUtils.byteslong(dataBodyBytes);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (data_type == 8) {
				// 返回{code 0} 验证头消息成功后返回
				String resultStr = "";
				try {
					resultStr = new String(dataBodyBytes, "utf-8");
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.info("服务器验证信息返回:{}", resultStr);
			}
		}

		// zlib解压
		if (data_ver == 2) {
			// 拆子包
			if (data_type == 5) {
				ParseWebsocketMessageUtils.parseUnZipMessage(ByteUtils.BytesTozlibInflate(dataBodyBytes));
			}

		}

		// brotli解压
		if(data_ver == 3){
			// 拆子包
			if(data_type==5){
				ParseWebsocketMessageUtils.parseUnZipMessage(ByteUtils.BytesToBrotliInflate(dataBodyBytes));
			}
		}
	}

	/**
	 * 处理解压后子数据包
	 * @param bytes
	 */
	public static void parseUnZipMessage(byte[] bytes) throws Exception {
		int offset = 0;
		int maxLen = bytes.length;

		while (offset < maxLen) {

			byte[] subBytes = ByteUtils.subBytes(bytes, offset, maxLen - offset);

			DanmuByteDataHandle danmuByteDataHandle = unBEhandle(subBytes);
			int data_len = danmuByteDataHandle.getPackageLength();
			int head_len = danmuByteDataHandle.getPackageHeadLength();
			int data_ver = danmuByteDataHandle.getPackageVersion();
			int data_type = danmuByteDataHandle.getPackageType();
			int data_other = danmuByteDataHandle.getPackageOther();

			byte[] subBodyBytes = ByteUtils.subBytes(subBytes, head_len, data_len - head_len);

			if (data_ver == 0) {
				try {

					String resultStr = new String(subBodyBytes, "utf-8");

					GlobalSettingCache.danmuList.add(resultStr);

					if (ObjectUtils.isNotEmpty(GlobalSettingCache.parseDanmuMessageThread) && !GlobalSettingCache.parseDanmuMessageThread.closeFlag) {
						synchronized (GlobalSettingCache.parseDanmuMessageThread) {
							GlobalSettingCache.parseDanmuMessageThread.notify();
						}
					}
				} catch (Exception e) {
					log.info("放入待处理弹幕集合，并通知线程处理，过程异常",e );
				}
			}

			if (data_ver == 1) {

				// 房间人气
				if (data_type == 3) {
					try {
						GlobalSettingCache.ROOM_POPULARITY = ByteUtils.byteslong(subBodyBytes);
					} catch (Exception e) {
						log.info("房间人气解析异常", e);
					}
				}
			}

			if (data_ver == 2) {
				if (data_type == 5) {
				}
			}

			offset += data_len;
		}
	}

	/**
	 * 处理弹幕数据
	 * 
	 * @param bytes 数据集
	 * @return
	 */
	public static DanmuByteDataHandle unBEhandle(byte[] bytes) {
		DanmuByteDataHandle danmuByteDataHandle = DanmuByteDataHandle.getBarrageHeadHandle();
		try {
			JavaStruct.unpack(danmuByteDataHandle, bytes);
		} catch (StructException e) {
			e.printStackTrace();
		}
		return danmuByteDataHandle;
	}
	
	/**
	 * 弹幕集打包
	 * @param danmuByteDataHandle
	 * @return
	 */
	public static byte[] BEhandle(DanmuByteDataHandle danmuByteDataHandle) {
		byte[] b = null;
		try {
			b = JavaStruct.pack(danmuByteDataHandle);
		} catch (StructException e) {
			e.printStackTrace();
		}
		return b;
	}
}
