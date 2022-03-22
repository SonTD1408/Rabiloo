package com.namtg.egovernment.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestApiAuthenticationSuccessHandler restApiAuthenticationSuccessHandler;

    @Autowired
    private RestApiAuthenticationFailureHandler restApiAuthenticationFailureHandler;

    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/api/**").permitAll()

                .antMatchers("/api/admin/**").hasAnyRole(new String[]{"GOVERNMENT", "PROVINCE", "DISTRICT", "COMMUNE"})
                .antMatchers("/admin/**").hasAnyRole(new String[]{"GOVERNMENT", "PROVINCE", "DISTRICT", "COMMUNE"});
        http.formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/login")
                .successHandler(restApiAuthenticationSuccessHandler)
                .failureHandler(restApiAuthenticationFailureHandler)
                .permitAll();
        http.exceptionHandling()
                .accessDeniedPage("/403");

        http.logout().
                logoutUrl("/logout").
                logoutSuccessHandler(customLogoutSuccessHandler);

        http.authenticationProvider(customAuthenticationProvider);

        http.headers().frameOptions().disable();

    }

    @Configuration
    static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {
        @Bean
        BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

    }

}
