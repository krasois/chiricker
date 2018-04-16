package com.chiricker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(@Qualifier(value = "userService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setSessionAttributeName("_csrf");
        return repository;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/register", "/assets/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin().loginPage("/login").permitAll()
                    .usernameParameter("handle")
                    .passwordParameter("password")
                .and()
                    .logout().logoutSuccessUrl("/").permitAll()
                .and()
                    .exceptionHandling().accessDeniedPage("/unauthorized")
                .and()
                    .csrf().csrfTokenRepository(this.csrfTokenRepository())
                .and()
                    .rememberMe()
                    .rememberMeParameter("remember")
                    .key("a31239e9-afbd-4a0f-9bac-43f532c12f55")
                    .rememberMeCookieName("rememberMe")
                    .tokenValiditySeconds(10000)
                    .userDetailsService(this.userDetailsService);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MODERATOR > ROLE_USER");
        return roleHierarchy;
    }
}
