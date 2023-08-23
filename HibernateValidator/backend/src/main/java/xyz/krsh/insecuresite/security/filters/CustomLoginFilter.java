package xyz.krsh.insecuresite.security.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.krsh.insecuresite.security.inputValidation.HibernateValidationAuthenticationForm;

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
         * If input is not valid, don't pass the response to the filterChain
         */
        if (request.getRequestURI().equals("/api/perform_login")) {
            HibernateValidationAuthenticationForm validator = new HibernateValidationAuthenticationForm();
            if (validator.testAuthenticationForm(request.getParameterMap()) == true) {
                filterChain.doFilter(request, response);
            } else {
                return;
            }
        } else {
            // Else, ok
            filterChain.doFilter(request, response);
        }

    }

}
