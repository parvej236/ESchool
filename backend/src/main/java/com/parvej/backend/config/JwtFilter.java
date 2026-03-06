package com.parvej.backend.config;

import com.parvej.backend.user.MyUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(token);
            } catch (ExpiredJwtException e) {
                // Token is expired — send a clean JSON response so the
                // frontend knows to use the refresh token
                sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "TOKEN_EXPIRED", "Access token expired. Please refresh.");
                return;
            } catch (Exception e) {
                // Malformed / invalid token
                sendJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "TOKEN_INVALID", "Invalid token.");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = context
                        .getBean(MyUserDetailsService.class)
                        .loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (UsernameNotFoundException e) {
                // User was deleted from DB but token still valid — deny silently
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Write a JSON error body instead of an empty 401/403
     */
    private void sendJsonError(
            HttpServletResponse response,
            int status,
            String code,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
                Map.of("success", false, "code", code, "error", message));
    }
}