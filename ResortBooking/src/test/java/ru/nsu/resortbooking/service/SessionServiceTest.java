package ru.nsu.resortbooking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.nsu.resortbooking.model.Session;
import ru.nsu.resortbooking.repository.SessionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void getAllSessions() {
        when(sessionRepository.findAll(any(Sort.class))).thenReturn(List.of(
                new Session(), new Session()
        ));

        List<Session> sessions = sessionService.getAllSessions();
        assertEquals(2, sessions.size());
        verify(sessionRepository).findAll(Sort.by("startDate"));
    }

    @Test
    void getByIdFound() {
        Session session = new Session();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);
        assertSame(session, result);
    }

    @Test
    void getByIdNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sessionService.getById(1L)
        );
        assertEquals("Session not found", exception.getMessage());
    }

    @Test
    void incrementBookedCountSuccessfully() {
        Session session = Session.builder()
                .capacity(10)
                .bookedCount(5)
                .build();

        sessionService.incrementBookedCount(session);
        assertEquals(6, session.getBookedCount());
        verify(sessionRepository).save(session);
    }

    @Test
    void incrementBookedCountWhenFull() {
        Session session = Session.builder()
                .capacity(10)
                .bookedCount(10)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> sessionService.incrementBookedCount(session)
        );
        assertEquals("Session is full", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void decrementBookedCount() {
        Session session = Session.builder()
                .bookedCount(5)
                .build();

        sessionService.decrementBookedCount(session);
        assertEquals(4, session.getBookedCount());
        verify(sessionRepository).save(session);
    }

    @Test
    void decrementBookedCountToZero() {
        Session session = Session.builder()
                .bookedCount(0)
                .build();

        sessionService.decrementBookedCount(session);
        assertEquals(0, session.getBookedCount());
        verify(sessionRepository).save(session);
    }

    @Test
    void saveSession() {
        Session session = new Session();
        when(sessionRepository.save(session)).thenReturn(session);

        Session saved = sessionService.save(session);
        assertSame(session, saved);
    }
}