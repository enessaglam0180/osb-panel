package com.osb.panel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login.xhtml",
                    "/jakarta.faces.resource/**",
                    "/h2-console/**",
                    "/css/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.xhtml", true)
                .failureUrl("/login.xhtml?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login.xhtml?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
                .disable()
            )
            .headers(headers -> headers.frameOptions(f -> f.disable()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN")
            .build();

        var personel = User.builder()
            .username("personel")
            .password(passwordEncoder().encode("personel123"))
            .roles("PERSONEL")
            .build();

        return new InMemoryUserDetailsManager(admin, personel);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
