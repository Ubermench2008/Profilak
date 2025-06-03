package ru.nsu.resortbooking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
  name = "booking_requests",
  uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "session_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // кто подал заявку
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // на какой сеанс
    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id")
    private Session session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false)
    private Boolean isPaid;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
