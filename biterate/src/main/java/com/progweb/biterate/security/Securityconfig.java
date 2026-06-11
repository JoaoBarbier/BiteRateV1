package com.progweb.biterate.security;

import com.progweb.biterate.repository.Clienterepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // Ativa a segurança web do Spring Security
@RequiredArgsConstructor
public class Securityconfig {

    private final Jwtfilter jwtFilter;
    private final Clienterepository clienteRepository;

    // Ignora a segurança do Spring para o console do banco H2 (evita bloqueios de tela)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/h2-console/**");
    }

    // Configuração principal de filtros e regras de acesso das URLs
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Aplica regras de CORS
                .csrf(AbstractHttpConfigurer::disable) // Desativa CSRF (desnecessário usando JWT/Stateless)
                .headers(h -> h.frameOptions(f -> f.sameOrigin())) // Permite renderizar iframes do mesmo domínio (necessário para o H2)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define que a API não guarda sessão em memória
                .authorizeHttpRequests(auth -> auth
                        // Libera o Swagger UI e a documentação da API
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // Libera os endpoints de login e cadastro
                        .requestMatchers("/api/auth/**").permitAll()
                        // Libera consultas públicas (GET) para restaurantes e avaliações recentes
                        .requestMatchers(HttpMethod.GET,
                                "/api/restaurantes/buscar",
                                "/api/restaurantes/{id}",
                                "/api/restaurantes/{id}/avaliacoes",
                                "/api/avaliacoes/recentes"
                        ).permitAll()
                        // Qualquer outra rota exige que o usuário esteja autenticado
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider()) // Define o provedor de autenticação usado
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Injeta o filtro JWT antes do filtro padrão do Spring
                .build();
    }

    // Configura o CORS para aceitar requisições de qualquer origem externa
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Define como o Spring vai buscar as credenciais do usuário pelo e-mail no banco de dados
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> clienteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado: " + email));
    }

    // Une o buscador de usuários (UserDetailsService) e o codificador de senhas
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // Expõe o gerenciador de autenticação padrão para ser usado na rota de Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Define o algoritmo BCrypt para criptografar e validar as senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}