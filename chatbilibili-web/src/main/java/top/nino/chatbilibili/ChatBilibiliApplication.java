package top.nino.chatbilibili;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


/**
 * @author nino
 */
@ServletComponentScan
@SpringBootApplication
public class ChatBilibiliApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(ChatBilibiliApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}


}
