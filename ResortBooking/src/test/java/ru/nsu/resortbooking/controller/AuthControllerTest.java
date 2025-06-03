package ru.nsu.resortbooking.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.model.UserRole;
import ru.nsu.resortbooking.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerSuccessfully() {
        AuthController.RegisterRequest registerRequest = new AuthController.RegisterRequest();
        registerRequest.email = "test@mail.ru";
        registerRequest.password = "password";
        registerRequest.role = UserRole.STAFF;

        User user = User.builder()
                .id(1L)
                .email("test@mail.ru")
                .role(UserRole.STAFF)
                .build();

        when(authService.register(any(), any(), any())).thenReturn(user);

        ResponseEntity<?> response = authController.register(registerRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.get("id"));
        assertEquals("test@mail.ru", body.get("email"));
        assertEquals(UserRole.STAFF, body.get("role"));
    }

    @Test
    void loginSuccessfully() {
        AuthController.LoginRequest loginRequest = new AuthController.LoginRequest();
        loginRequest.email = "user@mail.ru";
        loginRequest.password = "password";

        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user@mail.ru",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STAFF"))
        );

        when(authManager.authenticate(any())).thenReturn(auth);
        when(request.getSession(true)).thenReturn(session);

        ResponseEntity<?> response = authController.login(loginRequest, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("user@mail.ru", body.get("email"));
        assertEquals("ROLE_STAFF", body.get("role"));

        verify(session).setAttribute(any(), any());
    }

    @Test
    void logoutSuccessfully() {
        when(request.getSession()).thenReturn(session);

        ResponseEntity<?> response = authController.logout(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(session).invalidate();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void whoamiAuthenticated() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user@mail.ru",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );

        ResponseEntity<?> response = authController.whoami(auth);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("user@mail.ru", body.get("email"));
    }

    @Test
    void whoamiUnauthenticated() {
        ResponseEntity<?> response = authController.whoami(null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}