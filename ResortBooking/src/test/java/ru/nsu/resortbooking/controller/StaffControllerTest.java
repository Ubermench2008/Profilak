package ru.nsu.resortbooking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.nsu.resortbooking.model.BookingRequest;
import ru.nsu.resortbooking.model.Document;
import ru.nsu.resortbooking.model.RequestStatus;
import ru.nsu.resortbooking.model.Session;
import ru.nsu.resortbooking.repository.BookingRequestRepository;
import ru.nsu.resortbooking.repository.DocumentRepository;
import ru.nsu.resortbooking.service.RequestService;
import ru.nsu.resortbooking.service.SessionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class StaffControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RequestService requestService;

    @Mock
    private SessionService sessionService;

    @Mock
    private BookingRequestRepository requestRepository;

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private StaffController staffController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(staffController).build();
    }

    @Test
    void getAllRequestsWithStatus() {
        BookingRequest request = new BookingRequest();
        when(requestService.getByStatus(RequestStatus.PENDING)).thenReturn(List.of(request));

        List<BookingRequest> result = staffController.getAllRequests(RequestStatus.PENDING);
        assertEquals(1, result.size());
    }

    @Test
    void approveRequest() {
        BookingRequest request = new BookingRequest();
        when(requestService.approve(1L)).thenReturn(request);

        ResponseEntity<BookingRequest> response = staffController.approve(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void createValidSession() {
        Session session = Session.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .capacity(10)
                .build();

        when(sessionService.save(any())).thenReturn(session);

        ResponseEntity<Session> response = staffController.createSession(session);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void createInvalidSession() {
        Session session = Session.builder()
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().minusDays(1))
                .build();

        ResponseEntity<Session> response = staffController.createSession(session);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getDocuments_ShouldReturnDocumentsList() throws Exception {
        BookingRequest request = new BookingRequest();
        Document document = new Document();
        document.setFileName("test.pdf");

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(documentRepository.findByBookingRequestId(1L)).thenReturn(List.of(document));

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/staff/requests/1/documents")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fileName").value("test.pdf"));
    }

    @Test
    void approveRequest_ShouldChangeStatusToApproved() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setStatus(RequestStatus.APPROVED);

        when(requestService.approve(1L)).thenReturn(request);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/staff/requests/1/approve")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void rejectShouldChangeStatus() {
        BookingRequest request = new BookingRequest();
        when(requestService.reject(1L)).thenReturn(request);

        ResponseEntity<BookingRequest> response = staffController.reject(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}