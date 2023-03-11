package ru.practicum.ewm.main_service.event_category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<EventCategory, Long> {
    Optional<EventCategory> findByName(String name);
}
