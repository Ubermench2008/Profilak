package ru.nsu.resortbooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.model.UserRole;
import ru.nsu.resortbooking.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerStudentWithValidEmail() {
        when(userRepository.findByEmail("student@g.nsu.ru")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.register("student@g.nsu.ru", "password123", UserRole.STUDENT);

        assertNotNull(user);
        assertEquals("student@g.nsu.ru", user.getEmail());
        assertEquals("hashedPassword", user.getPasswordHash());
        assertEquals(UserRole.STUDENT, user.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerStudentWithInvalidEmail() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register("invalid@mail.com", "password", UserRole.STUDENT)
        );
        assertEquals("Student email must end with @g.nsu.ru", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerWithExistingEmail() {
        when(userRepository.findByEmail("existing@mail.ru")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register("existing@mail.ru", "password", UserRole.STAFF)
        );
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerStaffSuccessfully() {
        when(userRepository.findByEmail("staff@mail.ru")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("staffPass")).thenReturn("hashedStaffPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = authService.register("staff@mail.ru", "staffPass", UserRole.STAFF);

        assertNotNull(user);
        assertEquals("staff@mail.ru", user.getEmail());
        assertEquals("hashedStaffPass", user.getPasswordHash());
        assertEquals(UserRole.STAFF, user.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }
}