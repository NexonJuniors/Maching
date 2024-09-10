package NexonJuniors.Maching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WevSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authHttp -> authHttp
                                .anyRequest()
                                    .permitAll() // 모든 URL 모든 사용자에게 허용

//                                .requestMatchers()
//                                  .anonymous()
//                                .requestMatchers()
//                                  .authenticated()
                );

        return http.build();
    }
}
