package server.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import server.utils.TokenUtils;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    private static String normalizujPathZaMatch(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return "";
        }
        int semi = path.indexOf(';');
        if (semi >= 0) {
            path = path.substring(0, semi);
        }
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }
        if (path.isEmpty()) {
            return "/";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        while (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private static boolean isJavnaGostIliPlacanjeNarudzbine(HttpServletRequest request) {
        String path = normalizujPathZaMatch(request);
        String m = request.getMethod();
        if (path.startsWith("/api/narudzbina/guest")) {
            // Pod /guest samo očekivane metode; širi match od tačnog regex-a (npr. trailing /).
            return "GET".equals(m) || "POST".equals(m) || "HEAD".equals(m) || "OPTIONS".equals(m);
        }
        if (path.matches("/api/narudzbina/\\d+/pay-mock")
                || path.matches("/api/narudzbina/\\d+/paypal/create-order")
                || path.matches("/api/narudzbina/\\d+/paypal/capture")) {
            return "POST".equals(m) || "OPTIONS".equals(m);
        }
        return false;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());

        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get("bcrypt"));
        return passwordEncoder;
    }

    @Bean
    public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration conf) throws Exception {
        return conf.getAuthenticationManager();
    }

    @Bean
    public AuthenticationFilterBean getAuthenticationFilterBean(
            UserDetailsService userDetailsService,
            TokenUtils tokenUtils) {

        AuthenticationFilterBean filter = new AuthenticationFilterBean();
        // NEMA više setAuthenticationManager — nije potreban uz OncePerRequestFilter
        filter.setUserDetailsService(userDetailsService);
        filter.setTokenUtils(tokenUtils);
        return filter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationFilterBean filter) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ploca", "/api/ploca/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/zanr", "/api/zanr/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/slike", "/api/slike/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/proizvod", "/api/proizvod/**").permitAll()
                .requestMatchers(SecurityConfiguration::isJavnaGostIliPlacanjeNarudzbine)
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/narudzbina/moje").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/narudzbina").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register-admin").permitAll()
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
