package org.codelogger;

import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codelogger.utils.IOUtils;
import org.codelogger.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by defei on 8/13/17.
 */
@RestController
@SpringBootApplication
public class ProxyApplication {

    @Autowired
    private Config config;

    @RequestMapping(value = "download")
    public void available(String targetResource, HttpServletRequest httpServletRequest, HttpServletResponse httpResponse) throws IOException {

        if ((StringUtils.isNotBlank(targetResource) && config.isInWhitelist(httpServletRequest.getRemoteAddr()))) {
            String fixedTargetResource = URLDecoder.decode(targetResource, "UTF-8");
            IOUtils.write(IOUtils.getInputStreamFromNetwork(fixedTargetResource), httpResponse.getOutputStream());
        }
        httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }
}
