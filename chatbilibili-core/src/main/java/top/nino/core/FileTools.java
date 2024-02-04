package top.nino.core;

import org.springframework.boot.system.ApplicationHome;

import java.io.File;


/**
 * @author nino
 */
public class FileTools {
	public File getBaseJarPath() {
		ApplicationHome home = new ApplicationHome(getClass());
		File jarFile = home.getSource();
		return jarFile.getParentFile();
	}
}
