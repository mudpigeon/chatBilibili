package top.nino.chatbilibili.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : nino
 * @date : 2024/2/4 20:48
 */
@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping("/word")
    public void hello() {
        System.out.println("helloWorld");
    }


}
