package top.nino.api.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author : zengzhongjie
 * @date : 2024/2/3 01:41
 */
@Data
public class ChatResVo {
    private String role;
    private List<String> answers;
}
