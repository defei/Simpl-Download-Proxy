package org.codelogger;

import java.util.Objects;
import org.codelogger.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by defei on 8/13/17.
 */
@Configuration
public class Config {

    protected Boolean isInWhitelist(String ip) {

        String enableWhitelist = environment.getProperty("proxy.whitelist.enable");
        if(enableWhitelist == null){
            return false;
        }
        if (!enableWhitelist.equalsIgnoreCase("true")) {
            return true;
        } else {
            String whitelistIPs = environment.getProperty("proxy.whitelist.ips");
            if (StringUtils.isNotBlank(whitelistIPs)) {
                String[] whitelistIPArray = whitelistIPs.split(",");
                for (String whitelistIP : whitelistIPArray) {
                    if (Objects.equals(whitelistIP, ip)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public Config(@Autowired Environment environment) {
        this.environment = environment;
    }

    private Environment environment;

}
