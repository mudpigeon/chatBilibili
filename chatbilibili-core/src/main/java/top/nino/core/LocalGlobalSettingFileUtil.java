package top.nino.core;



import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author nino
 */
@Slf4j
public class LocalGlobalSettingFileUtil {

	// 存入 jar包的绝对路径
	private static final String ABSOLUTE_JAR_PATH;
	static {
		FileTools fileTools = new FileTools();
		String tmp;
		try {
			tmp = URLDecoder.decode(fileTools.getBaseJarPath().toString(), "utf-8");
		} catch (Exception e1) {
			log.warn(e1.getMessage(), e1);
			tmp = System.getProperty("user.dir");
		}
		ABSOLUTE_JAR_PATH = tmp;
	}

	/**
	 * 读取profile文件内容 转为 Map对象
	 * @param fileName 文件名称,非绝对地址
	 * @return is not null
	 * @throws IOException io流处理异常
	 * @throws FileNotFoundException 文件未找到
	 */
	public static Map<String, String> readFile(String fileName) throws IOException{
		Map<String, String> profileMap = new ConcurrentHashMap<>();
		new File(ABSOLUTE_JAR_PATH).mkdirs();

		File file = new File(ABSOLUTE_JAR_PATH + "/" + fileName);
		if (file.createNewFile()){
			// 如果成功创建，即之前没有配置文件
			return profileMap;
		}

		String dataString;
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
			while ((dataString = bufferedReader.readLine()) != null) {
				String[] strings = dataString.split(":@:");
				if (strings.length == 2) {
					profileMap.put(strings[0], strings[1]);
				}
			}
		} catch (FileNotFoundException e) {
			log.warn("文件{}不存在!", file.getAbsolutePath(), e);
		} catch(IOException e) {
			log.error(e.getMessage(), e);
		}
		return profileMap;
	}

	public static void writeFile(String fileName, Map<String, String> localGlobalSettingMap) {

		new File(ABSOLUTE_JAR_PATH).mkdirs();

		File file = new File(ABSOLUTE_JAR_PATH + "/" + fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)){

			BufferedWriter bufferedWriter = new BufferedWriter(os);
			StringBuffer stringBuffer = new StringBuffer();
			// 把 map 中的配置 全写到文件中
			localGlobalSettingMap.forEach((k, v) -> {
				stringBuffer.append(k).append(":@:").append(v).append("\r\n");
			});

			bufferedWriter.write(stringBuffer.toString());
			os.flush();
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
