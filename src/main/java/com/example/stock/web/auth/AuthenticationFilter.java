package com.example.stock.web.auth;

import com.example.stock.service.AdminAuthenticationService.AuthenticatedAdmin;
import com.example.stock.service.AuthenticationService.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 認証が必要なリソースへのアクセスを制御するフィルターです。
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH = "/login.html";
    private static final String INDEX_PATH = "/index.html";
    private static final String ADMIN_LOGIN_PATH = "/admin/login.html";
    private static final String ADMIN_INDEX_PATH = "/admin/index.html";
    private static final String ADMIN_ROOT_PATH = "/admin/";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute(SessionAttributes.AUTHENTICATED_USER) instanceof AuthenticatedUser;
        boolean adminAuthenticated = session != null && session.getAttribute(SessionAttributes.ADMIN_AUTHENTICATED_USER) instanceof AuthenticatedAdmin;

        if (!authenticated && isProtectedPage(path)) {
            response.sendRedirect(LOGIN_PATH);
            return;
        }

        if (!authenticated && !adminAuthenticated && requiresApiAuthentication(path)) {
            respondUnauthorized(response);
            return;
        }

        if (authenticated && path.equals(LOGIN_PATH)) {
            response.sendRedirect(INDEX_PATH);
            return;
        }

        if (path.equals("/") && !authenticated) {
            response.sendRedirect(LOGIN_PATH);
            return;
        }

        if (path.equals("/") && authenticated) {
            response.sendRedirect(INDEX_PATH);
            return;
        }

        if (!adminAuthenticated && isAdminProtectedPage(path)) {
            response.sendRedirect(ADMIN_LOGIN_PATH);
            return;
        }

        if (!adminAuthenticated && requiresAdminApiAuthentication(path)) {
            respondUnauthorized(response);
            return;
        }

        if (adminAuthenticated && path.equals(ADMIN_LOGIN_PATH)) {
            response.sendRedirect(ADMIN_INDEX_PATH);
            return;
        }

        if ((path.equals("/admin") || path.equals(ADMIN_ROOT_PATH)) && !adminAuthenticated) {
            response.sendRedirect(ADMIN_LOGIN_PATH);
            return;
        }

        if ((path.equals("/admin") || path.equals(ADMIN_ROOT_PATH)) && adminAuthenticated) {
            response.sendRedirect(ADMIN_INDEX_PATH);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPage(String path) {
        return path.equals(INDEX_PATH);
    }

    private boolean requiresApiAuthentication(String path) {
        return path.startsWith("/api/accounts") || path.equals("/api/auth/logout") || path.equals("/api/auth/session");
    }

    private boolean isAdminProtectedPage(String path) {
        return path.equals(ADMIN_INDEX_PATH);
    }

    private boolean requiresAdminApiAuthentication(String path) {
        return path.startsWith("/api/admin/") && !path.equals("/api/admin/auth/login");
    }

    private void respondUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"認証が必要です。\"}");
    }
}

