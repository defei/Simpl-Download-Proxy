package org.codelogger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codelogger.utils.Base64Util;
import org.codelogger.utils.IOUtils;
import org.codelogger.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.codelogger.utils.ExceptionUtils.iae;

/**
 * Created by defei on 8/13/17.
 */
@RestController
@SpringBootApplication
public class ProxyApplication {

    @Autowired
    private Config config;

    @RequestMapping(value = "download")
    public void available(String target, HttpServletRequest httpServletRequest, HttpServletResponse httpResponse) throws IOException {

        logger.info("Received download GET request with target:{}", target);
        try {
            if ((StringUtils.isNotBlank(target) && config.isInWhitelist(httpServletRequest.getRemoteAddr()))) {
                byte[] decode = Base64Util.decode(target);
                iae.throwIfNull(decode, "Parameter target must be base64 String.");
                String targetUrl = new String(decode);
                logger.info("Try to connect target {}", targetUrl);
                URL url = new URL(targetUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                logger.info("Connected try to write target bytes to response.");
                IOUtils.write(urlConnection.getInputStream(), httpResponse.getOutputStream());
            } else {
                logger.info("Blank targetResource[{}] or request ip not in whitelist.", target);
                httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Throwable e) {
            logger.warn("Got an exception when do proxy.", e);
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write(e.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

    private static final Logger logger = LoggerFactory.getLogger(ProxyApplication.class);
}
