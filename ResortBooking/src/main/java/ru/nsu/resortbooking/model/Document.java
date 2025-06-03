package ru.nsu.resortbooking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // к какой заявке относится
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id")
    private BookingRequest bookingRequest;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;
}
