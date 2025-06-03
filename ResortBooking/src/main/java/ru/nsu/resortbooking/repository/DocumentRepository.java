package ru.nsu.resortbooking.repository;

import ru.nsu.resortbooking.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByBookingRequestId(Long bookingRequestId);
}