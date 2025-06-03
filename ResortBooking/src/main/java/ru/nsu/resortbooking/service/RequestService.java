package ru.nsu.resortbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.resortbooking.model.BookingRequest;
import ru.nsu.resortbooking.model.RequestStatus;
import ru.nsu.resortbooking.model.Session;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.repository.BookingRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final BookingRequestRepository requestRepository;
    private final SessionService sessionService;

    @Transactional
    public BookingRequest createRequest(User user, Session session) {
        boolean exists = requestRepository
                .findByUserIdAndSessionId(user.getId(), session.getId())
                .isPresent();
        if (exists) {
            throw new IllegalArgumentException("Вы уже подали заявку на этот сеанс");
        }

        BookingRequest req = BookingRequest.builder()
                .user(user)
                .session(session)
                .status(RequestStatus.PENDING)
                .isPaid(false)
                .createdAt(LocalDateTime.now())
                .build();
        return requestRepository.save(req);
    }

    public List<BookingRequest> getByUser(Long userId) {
        return requestRepository.findByUserId(userId);
    }

    public List<BookingRequest> getByStatus(RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    @Transactional
    public BookingRequest approve(Long requestId) {
        BookingRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        sessionService.incrementBookedCount(req.getSession());
        req.setStatus(RequestStatus.APPROVED);
        return requestRepository.save(req);
    }

    @Transactional
    public BookingRequest reject(Long requestId) {
        BookingRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        req.setStatus(RequestStatus.REJECTED);
        return requestRepository.save(req);
    }

    @Transactional
    public BookingRequest markPaid(Long requestId) {
        BookingRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        req.setIsPaid(true);
        return requestRepository.save(req);
    }
}