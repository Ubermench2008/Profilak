package ru.nsu.resortbooking.repository;

import ru.nsu.resortbooking.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByStartDateAfter(LocalDate date);
}