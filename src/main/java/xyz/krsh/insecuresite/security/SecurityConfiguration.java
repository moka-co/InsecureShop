package xyz.krsh.insecuresite.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // User details service implemented through JPA repository pattern
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() { // Required, encode password with BCrypt
        // https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() { // Retrieve users from the database through UserDetails
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        // https://stackoverflow.com/questions/72381114/spring-security-upgrading-the-deprecated-websecurityconfigureradapter-in-spring
        return auth.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http builder configurations for authorize requests and form login (see below)
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/boardgames/*/delete")
                .hasAuthority("admin")
                .antMatchers("/api/boardgames/*/edit")
                .hasAuthority("admin")
                .antMatchers("/api/boardgames/add")
                .hasAuthority("admin")
                .antMatchers("/api/boardgames").permitAll()
                .antMatchers("/api/login*").permitAll()
                .and()
                .sessionManagement()
                .sessionFixation().none()
                .and()
                .formLogin()
                .loginProcessingUrl("/api/perform_login")
                .defaultSuccessUrl("/api/boardgames", true)
                .and()
                .logout() // //is not going to work because of HTTP
                .logoutUrl("/api/perform_logout") // https://stackoverflow.com/questions/5023951/spring-security-unable-to-logout?rq=3
                .logoutSuccessUrl("/login?logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .deleteCookies("JSESSIONID")
                .permitAll();
        return http.build();
    }
}