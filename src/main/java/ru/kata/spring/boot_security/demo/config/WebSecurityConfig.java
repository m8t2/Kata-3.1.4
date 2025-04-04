package ru.kata.spring.boot_security.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers(HttpMethod.GET, "/people/user").hasAuthority("ROLE_USER")
                .antMatchers(HttpMethod.GET, "/people/current").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .antMatchers("/people/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(successUserHandler)
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoderConfig.passwordEncoder());
        authenticationProvider.setUserDetailsService(securityService);
        return authenticationProvider;
    }
}