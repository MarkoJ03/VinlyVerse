package server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import server.utils.TokenUtils;

import java.io.IOException;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public class AuthenticationFilterBean extends OncePerRequestFilter {


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        while (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (path.startsWith("/api/narudzbina/guest")) {
            return true;
        }
        String m = request.getMethod();
        if (!"POST".equals(m) && !"OPTIONS".equals(m)) {
            return false;
        }
        if (path.matches("/api/narudzbina/\\d+/pay-mock")
                || path.matches("/api/narudzbina/\\d+/paypal/create-order")
                || path.matches("/api/narudzbina/\\d+/paypal/capture")) {
            return true;
        }
        return false;
    }

    private TokenUtils tokenUtils;
    private UserDetailsService userDetailsService;

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setTokenUtils(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;


        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token != null && tokenUtils.validateToken(token) && getContext().getAuthentication() == null) {
            String username = tokenUtils.getUsername(token);
            if (username != null) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }
}
