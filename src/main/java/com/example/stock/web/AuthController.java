package com.example.stock.web;

import com.example.stock.service.AuthenticationService;
import com.example.stock.service.AuthenticationService.AuthenticatedUser;
import com.example.stock.web.auth.SessionAttributes;
import com.example.stock.web.dto.LoginRequest;
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
 * 認証関連の API を提供するコントローラーです。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        AuthenticatedUser user = authenticationService.authenticate(request.email(), request.password())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "メールアドレスまたはパスワードが正しくありません。"));

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SessionAttributes.AUTHENTICATED_USER, user);
        return new LoginResponse(user.displayName());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
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
        Object attribute = session.getAttribute(SessionAttributes.AUTHENTICATED_USER);
        if (!(attribute instanceof AuthenticatedUser user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未認証です。");
        }
        return new SessionResponse(user.displayName());
    }
}

