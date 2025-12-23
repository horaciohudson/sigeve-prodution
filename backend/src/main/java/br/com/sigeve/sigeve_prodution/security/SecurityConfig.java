package br.com.sigeve.sigeve_prodution.security;




import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, 
                                          br.com.sigeve.sigeve_prodution.security.JwtService jwtService,
                                          AuthenticationProvider authenticationProvider) throws Exception {
        br.com.sigeve.sigeve_prodution.security.JwtAuthenticationFilter jwtFilter = new br.com.sigeve.sigeve_prodution.security.JwtAuthenticationFilter(jwtService);

        http
                .csrf(csrf -> {
                    csrf.disable();
                    System.out.println("🔓 CSRF desabilitado");
                })
                .cors(cors -> {
                    cors.configurationSource(corsConfigurationSource());
                    System.out.println("🌐 CORS configurado");
                })
                .authorizeHttpRequests(auth -> {
                    System.out.println("🔐 Configurando autorização HTTP:");
                    System.out.println("   ✅ /api/auth/** → PERMITIDO (sem autenticação)");
                    System.out.println("   ✅ /v3/api-docs/**, /swagger-ui/** → PERMITIDO");
                    System.out.println("   ✅ /, /index.html, /css/**, etc → PERMITIDO");
                    System.out.println("   🔒 Qualquer outra rota → REQUER AUTENTICAÇÃO");
                    
                    auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                    System.out.println("📝 Sessão: STATELESS (sem sessão)");
                })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        SecurityFilterChain chain = http.build();
        System.out.println("✅ SecurityFilterChain configurado com sucesso!");
        return chain;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}