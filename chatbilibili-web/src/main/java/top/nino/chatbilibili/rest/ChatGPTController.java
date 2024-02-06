package top.nino.chatbilibili.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.nino.api.model.vo.Response;
import top.nino.api.model.vo.dto.ChatResDto;
import top.nino.service.chatgpt.ChatGPTService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : nino
 * @date : 2024/2/3 01:15
 */
@Controller("/chatGPT")
public class ChatGPTController {

    @Autowired
    private ChatGPTService chatGPTService;


    @ResponseBody
    @GetMapping(value = "/test")
    public Response<?> test(HttpServletRequest req) {
        ChatResDto chatResDto = chatGPTService.chatCompletions("你好！");
        return Response.success(chatResDto, req);
    }

}
