package xyz.krsh.insecuresite.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

/*
 * Non funziona
 */
public class CustomLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            final Authentication authentication) throws IOException, ServletException {

        if (authentication != null) {
            System.out.println(authentication);
        }
        final String refererUrl = request.getHeader("referer");
        System.out.println(refererUrl);

        super.onLogoutSuccess(request, response, authentication);
    }

}
