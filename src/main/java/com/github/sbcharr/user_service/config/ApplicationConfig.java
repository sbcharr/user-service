package com.github.sbcharr.user_service.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationConfig {
    private final BeanFactory beanFactory;

    public ApplicationConfig(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(16);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable());
        httpSecurity.cors(cors -> cors.disable());
        httpSecurity.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated()
        );

        return httpSecurity.build();
    }
}
