package ru.nsu.resortbooking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.nsu.resortbooking.model.BookingRequest;
import ru.nsu.resortbooking.model.Document;
import ru.nsu.resortbooking.model.Session;
import ru.nsu.resortbooking.model.User;
import ru.nsu.resortbooking.repository.DocumentRepository;
import ru.nsu.resortbooking.service.RequestService;
import ru.nsu.resortbooking.service.SessionService;
import ru.nsu.resortbooking.service.UserService;
import ru.nsu.resortbooking.util.FileStorageUtil;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    private final UserService userService;
    private final SessionService sessionService;
    private final RequestService requestService;
    private final DocumentRepository documentRepository;
    private final FileStorageUtil fileStorageUtil;

    @GetMapping("/sessions")
    public List<Session> getAvailableSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/requests")
    public List<BookingRequest> getMyRequests(Authentication auth) {
        String email = auth.getName();
        User user = userService.getByEmail(email);
        return requestService.getByUser(user.getId());
    }

    @PostMapping(value = "/requests", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRequest(
            Authentication auth,
            @RequestParam Long sessionId,
            @RequestParam("files") MultipartFile[] files
    ) {
        try {
            String email = auth.getName();
            User user = userService.getByEmail(email);
            Session session = sessionService.getById(sessionId);
            BookingRequest req = requestService.createRequest(user, session);

            for (var file : files) {
                String path = fileStorageUtil.storeFile(file);
                Document doc = Document.builder()
                        .bookingRequest(req)
                        .fileName(file.getOriginalFilename())
                        .filePath(path)
                        .build();
                documentRepository.save(doc);
            }
            return ResponseEntity.ok(req);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/requests/{id}/pay")
    public ResponseEntity<BookingRequest> markPaid(@PathVariable Long id) {
        BookingRequest req = requestService.markPaid(id);
        return ResponseEntity.ok(req);
    }
}
