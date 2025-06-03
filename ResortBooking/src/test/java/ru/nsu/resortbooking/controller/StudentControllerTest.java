package ru.nsu.resortbooking.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import ru.nsu.resortbooking.model.*;
import ru.nsu.resortbooking.repository.DocumentRepository;
import ru.nsu.resortbooking.service.RequestService;
import ru.nsu.resortbooking.service.SessionService;
import ru.nsu.resortbooking.service.UserService;
import ru.nsu.resortbooking.util.FileStorageUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class StudentControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SessionService sessionService;

    @Mock
    private RequestService requestService;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FileStorageUtil fileStorageUtil;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StudentController studentController;

    @Test
    void getMyRequests() {
        when(authentication.getName()).thenReturn("test@mail.ru");
        User user = User.builder().id(1L).build();
        when(userService.getByEmail("test@mail.ru")).thenReturn(user);
        when(requestService.getByUser(1L)).thenReturn(List.of(new BookingRequest()));

        List<BookingRequest> result = studentController.getMyRequests(authentication);
        assertEquals(1, result.size());
    }

    @Test
    void createRequestWithFiles() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());

        when(authentication.getName()).thenReturn("test@mail.ru");
        User user = User.builder().id(1L).build();
        Session session = Session.builder().id(1L).build();
        BookingRequest request = BookingRequest.builder().id(1L).build();

        when(userService.getByEmail("test@mail.ru")).thenReturn(user);
        when(sessionService.getById(1L)).thenReturn(session);
        when(requestService.createRequest(user, session)).thenReturn(request);
        when(fileStorageUtil.storeFile(any())).thenReturn("stored_file.txt");

        ResponseEntity<?> response = studentController.createRequest(
                authentication, 1L, new MockMultipartFile[]{file});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(documentRepository, atLeastOnce()).save(any());
    }

    @Test
    void markPaidShouldUpdateStatus() {
        BookingRequest request = new BookingRequest();
        when(requestService.markPaid(1L)).thenReturn(request);

        ResponseEntity<BookingRequest> response = studentController.markPaid(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAvailableSessionsShouldReturnList() {
        when(sessionService.getAllSessions()).thenReturn(List.of(new Session()));

        List<Session> result = studentController.getAvailableSessions();
        assertFalse(result.isEmpty());
    }
}