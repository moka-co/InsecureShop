package xyz.krsh.insecuresite.security.filters;

import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.krsh.insecuresite.security.inputValidation.HttpBodyParser;
import xyz.krsh.insecuresite.security.inputValidation.TestESAPIValidator;

@Component
public class CustomLoginFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/perform_login")) {
            Map<String, String> formDataMap = new HttpBodyParser().convertHttpBodyToMap(request.getParameterMap());
            TestESAPIValidator validator = new TestESAPIValidator();
            validator.testIsValidEmail(formDataMap.get("username"));
        }
        filterChain.doFilter(request, response);

    }

}
