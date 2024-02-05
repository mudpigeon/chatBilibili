package top.nino.chatbilibili.service;

import org.springframework.stereotype.Service;
import top.nino.api.model.apex.ApexMessage;
import top.nino.api.model.apex.PredatorResult;
import top.nino.chatbilibili.http.HttpOtherData;
import top.nino.service.api.ApiService;

@Service
public class ApiServiceImpl implements ApiService {



    public PredatorResult getApexPredator(String key, String type) {
        PredatorResult predatorResult=null;

        return predatorResult;
    }
    public ApexMessage getApexMessage() {
        ApexMessage apexMessage=null;

        return apexMessage;
    }
}
