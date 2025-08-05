package server.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		Map<String, PasswordEncoder> encoders = new HashMap<String, PasswordEncoder>();
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
            AuthenticationConfiguration conf,
            UserDetailsService userDetailsService,
            TokenUtils tokenUtils) throws Exception {

        AuthenticationFilterBean filter = new AuthenticationFilterBean();
        filter.setAuthenticationManager(conf.getAuthenticationManager());
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
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration conf, AuthenticationFilterBean filter) throws Exception {
		http
			.cors(cors -> {})
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authz -> authz
				.requestMatchers(HttpMethod.GET, "/api/ploca/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/zanr/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/slike/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/proizvod/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/auth/register-admin").permitAll()
				.requestMatchers("/api/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//	    http
//	        .csrf(csrf -> csrf.disable())
//	        .authorizeHttpRequests(authz -> authz
//	            .anyRequest().permitAll()
//	        );
//	    return http.build();
//	}
}
