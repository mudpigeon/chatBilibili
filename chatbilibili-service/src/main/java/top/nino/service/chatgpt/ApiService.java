package top.nino.service.chatgpt;


import top.nino.api.model.apex.ApexMessage;
import top.nino.api.model.apex.PredatorResult;

public interface ApiService {



    PredatorResult getApexPredator(String key, String type);

    ApexMessage getApexMessage();
}
