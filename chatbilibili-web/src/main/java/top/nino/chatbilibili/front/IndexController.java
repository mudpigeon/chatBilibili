package top.nino.chatbilibili.front;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : zengzhongjie
 * @date : 2024/2/4 20:48
 */
@Slf4j
@Controller
@RequestMapping
public class IndexController {

    @GetMapping(value = {"/", "/index"})
    public String index(HttpServletRequest req, Model model) {
        return "index";
    }
}
