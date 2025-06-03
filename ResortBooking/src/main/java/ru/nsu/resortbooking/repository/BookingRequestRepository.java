package ru.nsu.resortbooking.repository;

import ru.nsu.resortbooking.model.BookingRequest;
import ru.nsu.resortbooking.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    List<BookingRequest> findByUserId(Long userId);
    List<BookingRequest> findByStatus(RequestStatus status);
    Optional<BookingRequest> findByUserIdAndSessionId(Long userId, Long sessionId);
}