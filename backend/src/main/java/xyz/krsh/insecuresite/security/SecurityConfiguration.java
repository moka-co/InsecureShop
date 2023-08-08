package xyz.krsh.insecuresite.security;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000/")); // Allow react front end
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true); // Cookies
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);
        return config;
    }

    @Bean
    public UrlBasedCorsConfigurationSource CustomCorsFilterSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration());
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http builder configurations for authorize requests and form login (see below)
        http.cors().configurationSource(CustomCorsFilterSource()).and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/boardgames/*/delete")
                .hasAuthority("admin")
                .antMatchers("/api/boardgames/*/edit")
                .hasAuthority("admin")
                .antMatchers("/api/boardgames/add")
                .hasAuthority("admin")
                .antMatchers("/api/boardgames/delete")
                .hasAuthority("admin")
                .antMatchers("/api/boardgames/edit")
                .hasAuthority("admin")
                .antMatchers("/api/orders/*")
                .authenticated()
                .antMatchers("/api/boardgames").permitAll()
                .antMatchers("/api/login*").permitAll()
                .antMatchers("/api/login_check/").permitAll()
                .antMatchers("/api/isAdmin").permitAll()
                .and()
                .sessionManagement()
                .sessionFixation().none()
                .and()
                .formLogin()
                .loginProcessingUrl("/api/perform_login").permitAll()
                .defaultSuccessUrl("http://localhost:3000/", true)
                .and()
                .logout() // //is not going to work because of HTTP
                .logoutUrl("/api/perform_logout") // https://stackoverflow.com/questions/5023951/spring-security-unable-to-logout?rq=3
                .logoutSuccessUrl("http://localhost:3000/") // React front end
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .deleteCookies("JSESSIONID")
                .permitAll();
        return http.build();
    }
}