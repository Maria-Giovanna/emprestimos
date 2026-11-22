package com.projeto.emprestimos.security;

import com.projeto.emprestimos.repository.ColaboradorRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    //ATRIBUTOS
    private final TokenService tokenService;

    //CONSTRUTOR
    public SecurityConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    //REGRAS
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())   // usa o CorsConfig: sem isso, o navegador toma 401 no "posso?"
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/error").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/equipamentos", "/equipamentos/*").permitAll() // o catálogo é público
                        .requestMatchers(HttpMethod.GET, "/colaboradores", "/colaboradores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET).authenticated()
                        .requestMatchers(HttpMethod.POST, "/emprestimos").authenticated()           // emprestar
                        .requestMatchers(HttpMethod.PUT, "/emprestimos/*/devolucao").authenticated() // devolver
                        .anyRequest().hasRole("ADMIN"))
                .exceptionHandling(erro -> erro.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(new FiltroJwt(tokenService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService usuarios(ColaboradorRepository repository) {
        return email -> repository.findByEmail(email)
                .map(colaborador -> User.withUsername(colaborador.getEmail())
                        .password(colaborador.getSenha())             // já é o hash BCrypt
                        .roles(colaborador.getPerfil().name())        // vira ROLE_ADMIN ou ROLE_COLABORADOR
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
