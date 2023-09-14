package xyz.krsh.insecuresite.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(2)
public class FilterConfiguration {

    /*
     * public FilterRegistrationBean<CookieValidationFilter> myFilter() {
     * FilterRegistrationBean<CookieValidationFilter> registrationBean = new
     * FilterRegistrationBean<>();
     * 
     * registrationBean.setFilter(new CookieValidationFilter());
     * registrationBean.addUrlPatterns("http://localhost:8080/*");
     * registrationBean.setOrder(2);
     * return registrationBean;
     * 
     * }
     */

}
