package top.nino.service.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : nino
 * @date : 2024/2/5 19:45
 */
@Slf4j
@Service
public class CommandServiceImpl implements CommandService{


    @Value("${server.port}")
    private int serverPort;

    @Override
    public void startApplication() {
        log.info(getIpAddressUrl());
        log.info(getLocalDomainUrl());
        // 默认浏览器打开网页
        try {
            Runtime rt = Runtime.getRuntime();
            if(SystemUtils.IS_OS_WINDOWS){
                rt.exec("rundll32 url.dll,FileProtocolHandler " + getLocalDomainUrl());
            }else if(SystemUtils.IS_OS_MAC){
                rt.exec("open " +  getLocalDomainUrl());
            }else if(SystemUtils.IS_OS_UNIX) {
                rt.exec("xdg-open " + getLocalDomainUrl());
            }else{
                log.error("自动打开浏览器错误:当前系统未知,打印系统:{}", System.getProperty("os.name").toLowerCase());
            }
        } catch (IOException e) {
            log.error("自动打开浏览器错误", e);
        }
    }


    public String getIpAddressUrl() {
        InetAddress address = null;
        String addressStr = "";
        try {
            address = InetAddress.getLocalHost();
            addressStr = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            addressStr = "获取失败";
        }
        return "http://"+ addressStr +":"+this.serverPort;
    }

    /**
     *
     * @return http://localhost:port
     */
    public String getLocalDomainUrl() {
        return "http://localhost:"  + this.serverPort;
    }

}
