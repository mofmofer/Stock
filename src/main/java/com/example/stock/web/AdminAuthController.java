package com.example.stock.web;

import com.example.stock.service.AdminAuthenticationService;
import com.example.stock.service.AdminAuthenticationService.AuthenticatedAdmin;
import com.example.stock.web.auth.SessionAttributes;
import com.example.stock.web.dto.AdminLoginRequest;
import com.example.stock.web.dto.LoginResponse;
import com.example.stock.web.dto.SessionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * 管理者向けの認証 API を提供するコントローラーです。
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminAuthenticationService adminAuthenticationService;

    public AdminAuthController(AdminAuthenticationService adminAuthenticationService) {
        this.adminAuthenticationService = adminAuthenticationService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody AdminLoginRequest request, HttpServletRequest httpRequest) {
        AuthenticatedAdmin admin = adminAuthenticationService.authenticate(request.adminId(), request.password())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "管理者 ID またはパスワードが正しくありません。"));

        HttpSession existingSession = httpRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SessionAttributes.ADMIN_AUTHENTICATED_USER, admin);
        return new LoginResponse(admin.displayName());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(SessionAttributes.ADMIN_AUTHENTICATED_USER);
            if (session.getAttributeNames().hasMoreElements()) {
                // Keep other session information if present (e.g. 利用者ログイン)
                return ResponseEntity.noContent().build();
            }
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/session")
    public SessionResponse session(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未認証です。");
        }
        Object attribute = session.getAttribute(SessionAttributes.ADMIN_AUTHENTICATED_USER);
        if (!(attribute instanceof AuthenticatedAdmin admin)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未認証です。");
        }
        return new SessionResponse(admin.displayName());
    }
}

