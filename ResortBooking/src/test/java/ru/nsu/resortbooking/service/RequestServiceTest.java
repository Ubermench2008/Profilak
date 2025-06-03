package ru.nsu.resortbooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nsu.resortbooking.model.BookingRequest;
import ru.nsu.resortbooking.model.RequestStatus;
import ru.nsu.resortbooking.model.Session;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.repository.BookingRequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private BookingRequestRepository requestRepository;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private RequestService requestService;

    private User createUser(Long id) {
        return User.builder().id(id).email("user@mail.ru").build();
    }

    private Session createSession(Long id, int capacity, int bookedCount) {
        return Session.builder()
                .id(id)
                .capacity(capacity)
                .bookedCount(bookedCount)
                .build();
    }

    @Test
    void createRequestSuccessfully() {
        User user = createUser(1L);
        Session session = createSession(1L, 10, 5);

        when(requestRepository.findByUserIdAndSessionId(1L, 1L)).thenReturn(Optional.empty());
        when(requestRepository.save(any(BookingRequest.class))).thenAnswer(invocation -> {
            BookingRequest req = invocation.getArgument(0);
            req.setId(1L);
            return req;
        });

        BookingRequest request = requestService.createRequest(user, session);

        assertNotNull(request);
        assertEquals(RequestStatus.PENDING, request.getStatus());
        assertFalse(request.getIsPaid());
        assertNotNull(request.getCreatedAt());
        verify(requestRepository, times(1)).save(any(BookingRequest.class));
    }

    @Test
    void createDuplicateRequest() {
        User user = createUser(1L);
        Session session = createSession(1L, 10, 5);

        when(requestRepository.findByUserIdAndSessionId(1L, 1L))
                .thenReturn(Optional.of(new BookingRequest()));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.createRequest(user, session)
        );
        assertEquals("Вы уже подали заявку на этот сеанс", exception.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void approveRequestSuccessfully() {
        Session session = createSession(1L, 10, 5);
        BookingRequest request = BookingRequest.builder()
                .id(1L)
                .session(session)
                .status(RequestStatus.PENDING)
                .build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenReturn(request);

        BookingRequest approved = requestService.approve(1L);

        assertEquals(RequestStatus.APPROVED, approved.getStatus());
        verify(sessionService).incrementBookedCount(session);
    }

    @Test
    void approveNonExistingRequest() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> requestService.approve(1L)
        );
        assertEquals("Request not found", exception.getMessage());
        verify(sessionService, never()).incrementBookedCount(any());
    }

    @Test
    void rejectRequestSuccessfully() {
        BookingRequest request = BookingRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenReturn(request);

        BookingRequest rejected = requestService.reject(1L);

        assertEquals(RequestStatus.REJECTED, rejected.getStatus());
        verify(sessionService, never()).incrementBookedCount(any());
    }

    @Test
    void markRequestPaid() {
        BookingRequest request = BookingRequest.builder()
                .id(1L)
                .isPaid(false)
                .build();

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any())).thenReturn(request);

        BookingRequest paid = requestService.markPaid(1L);

        assertTrue(paid.getIsPaid());
    }

    @Test
    void getByUserShouldReturnRequests() {
        when(requestRepository.findByUserId(1L)).thenReturn(List.of(new BookingRequest()));

        List<BookingRequest> result = requestService.getByUser(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void getByStatusShouldFilterRequests() {
        BookingRequest pendingRequest = new BookingRequest();
        pendingRequest.setStatus(RequestStatus.PENDING);

        when(requestRepository.findByStatus(RequestStatus.PENDING))
                .thenReturn(List.of(pendingRequest));

        List<BookingRequest> result = requestService.getByStatus(RequestStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(RequestStatus.PENDING, result.get(0).getStatus());
    }
}