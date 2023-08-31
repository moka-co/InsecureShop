package xyz.krsh.insecuresite.security.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Cookie;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.validation.StringValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import xyz.krsh.insecuresite.rest.entities.mongodb.ValidationRuleDocument;
import xyz.krsh.insecuresite.rest.repository.mongodb.ValidationRuleRepository;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;

@Component
public class ValidateCookieFilter extends OncePerRequestFilter {
    protected static final Logger logger = LogManager.getLogger();

    @Autowired
    ESAPIValidatorService validator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        StringValidationRule cookieRule = validator.getCookieValidationRule();

        Cookie[] cookies = request.getCookies();
        Stream<Cookie> stream = Objects.nonNull(cookies) ? Arrays.stream(cookies) : Stream.empty();
        stream.forEach(cookie -> {
            if (!cookieRule.isValid("Validating JSESSIONID", cookie.getValue())) {
                return;
            }
            if (cookie != null) {
                logger.info("JSESSIONID: " + cookie.getValue() + " - valid");
            }

        });

        filterChain.doFilter(request, response);
    }

}
