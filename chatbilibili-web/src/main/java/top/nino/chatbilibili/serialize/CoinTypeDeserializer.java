package top.nino.chatbilibili.serialize;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import top.nino.chatbilibili.tool.ParseIndentityTools;


import java.lang.reflect.Type;


/**
 * @author nino
 */
public class CoinTypeDeserializer implements ObjectDeserializer {

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String value = parser.parseObject(String.class);
        Short coin_type = ParseIndentityTools.parseCoin_type(value);
        return (T)coin_type;

    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
