package top.nino.core.file;


import top.nino.core.time.JodaTimeUtils;

import java.io.*;
import java.net.URLDecoder;


/**
 * @author nino
 */
public class LogFileUtils {


	public static void logFile(String msg, String globalSettingFileName, Long roomId) {
		OutputStreamWriter os= null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		String path = System.getProperty("user.dir");
		FileUtils fileUtils = new FileUtils();
		StringBuilder stringBuilder = new StringBuilder();
		try {
			path = URLDecoder.decode(fileUtils.getBaseJarPath().toString(), "utf-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			// 如果文件存在，则追加内容；如果文件不存在，则创建文件
			path = path + "/" + globalSettingFileName + "/";
			File file = new File(path);
			if (file.exists() == false) {
				file.mkdirs();
			}
			stringBuilder.append(JodaTimeUtils.getCurrentDateString());
			stringBuilder.append("(");
			stringBuilder.append(roomId);
			stringBuilder.append(")");
			file = new File(path + stringBuilder.toString() + ".txt");
			stringBuilder.delete(0, stringBuilder.length());
			if (file.exists() == false)
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			os = new OutputStreamWriter(new FileOutputStream(file,true),"utf-8");
			bw = new BufferedWriter(os);
			pw = new PrintWriter(bw);
			pw.println(msg);
			os.flush();
			bw.flush();
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
	}
}
