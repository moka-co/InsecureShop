package xyz.krsh.insecuresite.security;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class LoggerWrapper {
    protected static final Logger logger = LogManager.getLogger("Splunk Logger");
    protected static final Logger loggerStatus = LogManager.getLogger("Splunk logger status");

    public void log(HttpServletRequest request, String message) {
        if (request != null) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) { // if user is logged, log it
                ThreadContext.put("username", principal.getName());
            }

            // Log Ip address
            ThreadContext.put("IpAddress", request.getRemoteAddr());

            // Log Cookies
            Cookie[] cookieJar = request.getCookies();
            if (cookieJar != null && cookieJar.length > 0) {
                for (Cookie cookie : cookieJar) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        ThreadContext.put(cookie.getName(), cookie.getValue());
                    }
                }
            }

        }

        logger.info(message);
        ThreadContext.clearAll();
    }

    public void log(HttpServletRequest request, HttpServletResponse response) {
        if (request != null) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) { // if user is logged, log it
                ThreadContext.put("username", principal.getName());
            }

            // Log Ip address
            ThreadContext.put("IpAddress", request.getRemoteAddr());

            // Log Cookies
            Cookie[] cookieJar = request.getCookies();
            if (cookieJar != null && cookieJar.length > 0) {
                for (Cookie cookie : cookieJar) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        ThreadContext.put(cookie.getName(), cookie.getValue());
                    }
                }
            }
        }

        if (response != null) {
            ThreadContext.put("HttpStatus", String.valueOf(response.getStatus()));
            loggerStatus.info("Detected strange Status Code!");
        }

        ThreadContext.clearAll();

    }

}
