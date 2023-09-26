package xyz.krsh.insecuresite.security.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;
import xyz.krsh.insecuresite.security.LoggerWrapper;

@Component
public class ValidateLoginFormFilter extends OncePerRequestFilter {
    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    ESAPIValidatorService validator;

    /*
     * Intercepts login requests
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        /*
         * Test if email and password are valid during login
         * If input is not valid, don't pass the response to the filterChain
         */
        if (request.getRequestURI().equals("/api/perform_login")) {
            if (validator == null) { // Lazy loading the ESAPI Validator service class
                ServletContext servletContext = request.getServletContext();
                WebApplicationContext webApplicationContext = WebApplicationContextUtils
                        .getRequiredWebApplicationContext(servletContext);
                validator = webApplicationContext.getBean(ESAPIValidatorService.class);

            }

            LoggerWrapper loggerSplunk = new LoggerWrapper();

            if (validator.testAuthenticationForm(request.getParameterMap()) == true) {
                loggerSplunk.log("Login detected", request);
                filterChain.doFilter(request, response);
            } else {
                loggerSplunk.log("Login failed: invalid form", request, HttpStatus.UNAUTHORIZED.value());
                return;
            }
        } else {
            // Else, ok
            filterChain.doFilter(request, response);
        }

    }

}
