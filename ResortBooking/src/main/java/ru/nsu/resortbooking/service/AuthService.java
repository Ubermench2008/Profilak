package ru.nsu.resortbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.model.UserRole;
import ru.nsu.resortbooking.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String email, String rawPassword, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (role == UserRole.STUDENT && !email.endsWith("@g.nsu.ru")) {
            throw new IllegalArgumentException("Student email must end with @g.nsu.ru");
        }
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return userRepository.save(user);
    }
}