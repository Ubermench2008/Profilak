package ru.nsu.resortbooking.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {
    private final RequestService requestService;
    private final BookingRequestRepository requestRepository;
    private final DocumentRepository documentRepository;
    private final SessionService sessionService;

    @GetMapping("/requests")
    public List<BookingRequest> getAllRequests(
            @RequestParam(required = false) RequestStatus status
    ) {
        if (status != null) {
            return requestService.getByStatus(status);
        }
        return requestRepository.findAll();
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<BookingRequest> approve(@PathVariable Long id) {
        BookingRequest req = requestService.approve(id);
        return ResponseEntity.ok(req);
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<BookingRequest> reject(@PathVariable Long id) {
        BookingRequest req = requestService.reject(id);
        return ResponseEntity.ok(req);
    }

    @GetMapping("/sessions")
    public List<Session> listSessions() {
        return sessionService.getAllSessions();
    }

    @PostMapping("/sessions")
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        session.setBookedCount(0);
        if (session.getEndDate().isBefore(session.getStartDate())) {
            return ResponseEntity.badRequest().build();
        }
        Session saved = sessionService.save(session);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/requests/{id}/documents")
    public ResponseEntity<?> getDocuments(@PathVariable Long id) {
        BookingRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        List<Document> docs = documentRepository.findByBookingRequestId(id);
        return ResponseEntity.ok(docs);
    }
}
