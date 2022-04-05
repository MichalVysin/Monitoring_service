package com.example.monitoring_service.security;

import com.example.monitoring_service.security.jwt.JwtTokenVerifier;
import com.example.monitoring_service.security.jwt.JwtUsernameAndPassAuthFilter;
import com.example.monitoring_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtTokenVerifier jwtTokenVerifier;
    private final JwtUsernameAndPassAuthFilter jwtUsernameAndPassAuthFilter;

    @Autowired
    public SecurityConfig(PasswordEncoder passwordEncoder,
                          UserService userService,
                          JwtTokenVerifier jwtTokenVerifier,
                          @Lazy JwtUsernameAndPassAuthFilter jwtUsernameAndPassAuthFilter) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtTokenVerifier = jwtTokenVerifier;
        this.jwtUsernameAndPassAuthFilter = jwtUsernameAndPassAuthFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(jwtUsernameAndPassAuthFilter)
                .addFilterAfter(jwtTokenVerifier, JwtUsernameAndPassAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/registration", "/login").permitAll()
                .anyRequest()
                .authenticated();

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder)
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .jdbcAuthentication();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
