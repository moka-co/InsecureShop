package xyz.krsh.insecuresite.security;

import java.security.Principal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class LoggerWrapper {
    protected static final Logger logger = LogManager.getLogger("console-context");

    public void log(HttpServletRequest request, Principal principal, String message) {
        if (principal != null) {
            ThreadContext.put("username", principal.getName());
        }
        if (request != null && request.getCookies().length > 0) {
            Cookie jsessionid = request.getCookies()[0];
            ThreadContext.put("IpAddress", request.getRemoteAddr());
            ThreadContext.put(jsessionid.getName(), jsessionid.getValue());
        }

        logger.info(message);
    }

}
