package xyz.krsh.insecuresite.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(2)
public class FilterConfiguration {
    /*
     * public FilterRegistrationBean<MyTestFilter> myFilter() {
     * FilterRegistrationBean<MyTestFilter> registrationBean = new
     * FilterRegistrationBean<>();
     * 
     * registrationBean.setFilter(new MyTestFilter());
     * registrationBean.addUrlPatterns("http://localhost:8080/*");
     * registrationBean.setOrder(2);
     * return registrationBean;
     * 
     * }
     */

}
