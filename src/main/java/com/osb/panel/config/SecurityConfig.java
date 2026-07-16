package com.osb.panel.config;

import com.osb.panel.service.KullaniciService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        http
            .authenticationProvider(authProvider)
            .authorizeHttpRequests(auth -> auth
                // Public — login gerekmez
                .requestMatchers(
                    "/aday-basvuru.xhtml",
                    "/login.xhtml",
                    "/jakarta.faces.resource/**",
                    "/h2-console/**",
                    "/css/**",
                    "/uploads/**"
                ).permitAll()
                // İşveren sayfaları — ISVEREN veya OPERATOR erişebilir
                .requestMatchers("/isveren-paneli.xhtml").hasAnyRole("ISVEREN", "OPERATOR")
                // Operatör/Admin sayfaları — sadece OPERATOR erişebilir
                .requestMatchers(
                    "/ik-paneli.xhtml",
                    "/index.xhtml",
                    "/dashboard.xhtml"
                ).hasRole("OPERATOR")
                // Geri kalan her şey login gerektirir
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    // Role göre yönlendirme
                    boolean isOperator = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_OPERATOR"));
                    if (isOperator) {
                        response.sendRedirect("/dashboard.xhtml");
                    } else {
                        response.sendRedirect("/isveren-paneli.xhtml");
                    }
                })
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
    public DaoAuthenticationProvider authenticationProvider(KullaniciService kullaniciService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(kullaniciService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
