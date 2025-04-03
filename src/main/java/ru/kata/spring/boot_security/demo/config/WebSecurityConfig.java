package ru.kata.spring.boot_security.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ru.kata.spring.boot_security.demo.security.SecurityService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SuccessUserHandler successUserHandler;
    private final SecurityService securityService;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler, SecurityService securityService, PasswordEncoderConfig passwordEncoderConfig) {
        this.successUserHandler = successUserHandler;
        this.securityService = securityService;
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // CSRF disabled for simplicity (re-enable if needed)
                .authorizeRequests()
                .antMatchers("/public/**").permitAll()  // Public resources
                .anyRequest().authenticated()           // All other requests require authentication
                .and()
                .formLogin()  // Use Spring Security's default login
                .permitAll()  // Allow all to access the login page
                .and()
                .logout()  // Use Spring Security's default logout
                .permitAll();  // Allow all to access logout
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
        authenticationProvider.setUserDetailsService(securityService);
        return authenticationProvider;
    }
}