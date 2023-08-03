package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);
}
