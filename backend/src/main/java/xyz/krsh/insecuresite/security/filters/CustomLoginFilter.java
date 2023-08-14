package xyz.krsh.insecuresite.security.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.krsh.insecuresite.security.inputValidation.ESAPIAuthenticationFormValidator;

@Component
public class CustomLoginFilter extends OncePerRequestFilter {

    /*
     * Intercepts login requests
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        /*
         * Test if email and password are valid during login
         */
        if (request.getRequestURI().equals("/api/perform_login")) {
            ESAPIAuthenticationFormValidator validator = new ESAPIAuthenticationFormValidator();
            validator.testAuthenticationForm(request.getParameterMap());
        }
        filterChain.doFilter(request, response);

    }

}
