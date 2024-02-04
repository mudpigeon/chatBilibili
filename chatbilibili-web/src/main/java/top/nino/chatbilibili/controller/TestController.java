package top.nino.chatbilibili.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : zengzhongjie
 * @date : 2024/2/4 20:48
 */
@RestController
@RequestMapping("/hello")
public class TestController {

    @GetMapping("/word")
    public void hello() {
        System.out.println("helloWorld");
    }
}
