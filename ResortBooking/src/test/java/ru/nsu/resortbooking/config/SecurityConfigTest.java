package ru.nsu.resortbooking.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.model.UserRole;
import ru.nsu.resortbooking.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfig securityConfig;

    @Mock
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "STAFF")
    void staffAccessToStaffEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/staff/requests"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentAccessDeniedToStaffEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/staff/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void staffAccessDeniedToStudentEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    void forbiddenAccessDeniedToProtectedEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    void securityFilterChainBean() throws Exception {
        assertNotNull(securityConfig.securityFilterChain(mock(HttpSecurity.class)));
    }

    @Test
    void securityFilterChainShouldConfigureAuth() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        when(http.csrf(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);

        SecurityFilterChain chain = securityConfig.securityFilterChain(http);
        assertNotNull(chain);
    }

    @Test
    void userDetailsService_ShouldReturnUserDetails_WhenUserExists() {
        SecurityConfig securityConfig = new SecurityConfig(userRepository);
        User testUser = new User();
        testUser.setEmail("test@mail.ru");
        testUser.setPasswordHash("encodedPass");
        testUser.setRole(UserRole.STAFF);

        when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(testUser));

        var userDetailsService = securityConfig.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@mail.ru");

        assertEquals("test@mail.ru", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF")));
    }

    @Test
    void userDetailsService_ShouldThrow_WhenUserNotFound() {
        SecurityConfig securityConfig = new SecurityConfig(userRepository);
        when(userRepository.findByEmail("unknown@mail.ru")).thenReturn(Optional.empty());

        var userDetailsService = securityConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@mail.ru"));
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptEncoder() {
        SecurityConfig securityConfig = new SecurityConfig(userRepository);
        assertNotNull(securityConfig.passwordEncoder());
    }
}