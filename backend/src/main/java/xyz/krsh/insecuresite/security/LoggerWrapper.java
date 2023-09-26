package xyz.krsh.insecuresite.security;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class LoggerWrapper {
    protected static final Logger logger = LogManager.getLogger("Splunk Logger");

    public void log(String message, HttpServletRequest request) {
        if (request != null) {
            Principal principal = request.getUserPrincipal();
            if (principal != null) { // if user is logged, log it
                ThreadContext.put("username", principal.getName());
            }

            // Content Length
            String contentLength = request.getContentLength() > 0 ? String.valueOf(request.getContentLength())
                    : String.valueOf(0);
            ThreadContext.put("ContentLength", contentLength);

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

        ThreadContext.put("HttpStatus", String.valueOf(200));
        logger.info(message);
        ThreadContext.clearAll();
    }

    public void log(String message, HttpServletRequest request, int httpStatus) {
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

        if (httpStatus > 0 && httpStatus < 600) {
            ThreadContext.put("HttpStatus", String.valueOf(httpStatus));
        }
        logger.info(message);

        ThreadContext.clearAll();

    }

}
