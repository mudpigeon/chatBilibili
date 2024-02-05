package top.nino.service.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.nino.service.componect.ServerAddressComponent;

import java.io.IOException;

/**
 * @author : nino
 * @date : 2024/2/5 19:45
 */
@Slf4j
@Service
public class CommandServiceImpl implements CommandService{


    @Autowired
    private ServerAddressComponent serverAddressComponent;


    @Override
    public void startApplication() {
        log.info(serverAddressComponent.getIpAddressUrl());
        log.info(serverAddressComponent.getLocalDomainUrl());
        // 默认浏览器打开网页
        try {
            Runtime rt = Runtime.getRuntime();
            if(SystemUtils.IS_OS_WINDOWS){
                rt.exec("rundll32 url.dll,FileProtocolHandler " + serverAddressComponent.getLocalDomainUrl());
            }else if(SystemUtils.IS_OS_MAC){
                rt.exec("open " +  serverAddressComponent.getLocalDomainUrl());
            }else if(SystemUtils.IS_OS_UNIX) {
                rt.exec("xdg-open " + serverAddressComponent.getLocalDomainUrl());
            }else{
                log.error("自动打开浏览器错误:当前系统未知,打印系统:{}", System.getProperty("os.name").toLowerCase());
            }
        } catch (IOException e) {
            log.error("自动打开浏览器错误", e);
        }
    }
}
