package xyz.krsh.insecuresite.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import xyz.krsh.insecuresite.security.filters.ValidateLoginFormFilter;
import xyz.krsh.insecuresite.security.filters.ValidateCookieFilter;

@Configuration
@Order(2)
public class FilterConfiguration {

    public FilterRegistrationBean<ValidateCookieFilter> validateCookieFilter() {
        FilterRegistrationBean<ValidateCookieFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ValidateCookieFilter());
        registrationBean.addUrlPatterns("http://localhost:8080/*");
        registrationBean.setOrder(2);
        return registrationBean;

    }

    public FilterRegistrationBean<ValidateLoginFormFilter> validateLoginFilter() {
        FilterRegistrationBean<ValidateLoginFormFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ValidateLoginFormFilter());
        registrationBean.addUrlPatterns("http://localhost:8080/*");
        registrationBean.setOrder(2);
        return registrationBean;

    }

}
