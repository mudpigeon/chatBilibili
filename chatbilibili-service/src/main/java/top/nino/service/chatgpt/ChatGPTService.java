package top.nino.service.chatgpt;


import top.nino.api.model.vo.dto.ChatResDto;

/**
 * @author : zengzhongjie
 * @date : 2024/2/3 01:27
 */
public interface ChatGPTService {
    ChatResDto chatCompletions(String msg);
}
