package ru.nsu.resortbooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.model.UserRole;
import ru.nsu.resortbooking.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getByIdSuccess() {
        User user = User.builder().id(1L).email("test@mail.ru").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);
        assertEquals("test@mail.ru", result.getEmail());
    }

    @Test
    void getByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.getById(1L));
    }

    @Test
    void getByEmailSuccess() {
        User user = User.builder().id(1L).email("test@mail.ru").build();
        when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(user));

        User result = userService.getByEmail("test@mail.ru");
        assertEquals(1L, result.getId());
    }

    @Test
    void getAllStudents() {
        User student1 = User.builder().role(UserRole.STUDENT).build();
        User student2 = User.builder().role(UserRole.STUDENT).build();
        User staff = User.builder().role(UserRole.STAFF).build();

        when(userRepository.findAll()).thenReturn(List.of(student1, student2, staff));

        List<User> students = userService.getAllStudents();
        assertEquals(2, students.size());
        assertTrue(students.stream().allMatch(u -> u.getRole() == UserRole.STUDENT));
    }
}