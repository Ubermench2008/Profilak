package ru.nsu.resortbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.resortbooking.model.Session;
import ru.nsu.resortbooking.repository.SessionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

    public List<Session> getAllSessions() {
        return sessionRepository.findAll(Sort.by("startDate"));
    }

    public Session getById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    @Transactional
    public void incrementBookedCount(Session session) {
        if (session.getBookedCount() >= session.getCapacity()) {
            throw new IllegalArgumentException("Session is full");
        }
        session.setBookedCount(session.getBookedCount() + 1);
        sessionRepository.save(session);
    }

    @Transactional
    public void decrementBookedCount(Session session) {
        session.setBookedCount(Math.max(0, session.getBookedCount() - 1));
        sessionRepository.save(session);
    }

    @Transactional
    public Session save(Session session) {
        return sessionRepository.save(session);
    }
}
