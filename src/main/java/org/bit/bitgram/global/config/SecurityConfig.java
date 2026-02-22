package org.bit.bitgram.global.config;

import lombok.RequiredArgsConstructor;
import org.bit.bitgram.global.security.jwt.JwtFilter;
import org.bit.bitgram.global.security.jwt.TokenProvider;
import org.bit.bitgram.global.security.auth.CustomAuthUserService;
import org.bit.bitgram.global.security.auth.OAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomAuthUserService customAuthUserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
	        .cors(cors -> cors.configurationSource(request -> {
	            var config = new org.springframework.web.cors.CorsConfiguration();
	            config.setAllowedOrigins(java.util.List.of("http://localhost:5173", "http://localhost:3000")); // 프론트 주소
	            config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	            config.setAllowedHeaders(java.util.List.of("*"));
	            config.setAllowCredentials(true);
	            return config;
	        }))
            .csrf(AbstractHttpConfigurer::disable)
            
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/api/auth/me").authenticated()
            	.requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/reissue").permitAll() 
            	.requestMatchers("/login/oauth2/**", "/swagger-ui/**", "/v3/api-docs/**", "/", "/index.html").permitAll()
            	.anyRequest().authenticated() 
            )
            
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                        .userService(customAuthUserService) 
                    )
                    .successHandler(oAuth2SuccessHandler)
                )

            .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
