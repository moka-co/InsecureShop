package xyz.krsh.insecuresite.security.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MyTestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getMethod().equals("GET") && request.getRequestURI().contains("edit")) {
            String descr = request.getParameter("description");
            if (descr != null) {
                try {
                    descr = ESAPI.validator().getValidInput("boardgames", descr, "Description", 1024, false);
                    filterChain.doFilter(request, response);
                } catch (IntrusionException e) {
                    System.out.println("Intrusion detected");
                    // e.printStackTrace();
                } catch (ValidationException e) {
                    System.out.println("The string: \n[[" + descr + " ]]\ndoesnt not match the validation rule");
                }
            }

        }

        //Comment the follow to block request if exception occurrs
        filterChain.doFilter(request, response);
    }

}
