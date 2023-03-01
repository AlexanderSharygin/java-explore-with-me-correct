package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.dto.Stats;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.ewm.dto.Stats(a.name, h.uri, COUNT (h.id)) " +
            "FROM Hit h JOIN App a ON a.id=h.app.id " +
            "WHERE h.dateTime BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY  h.uri, a.name " +
            "ORDER BY COUNT(h.id) DESC")
    List<Stats> findAllHits(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT new ru.practicum.ewm.dto.Stats(a.name, h.uri, count (h.id))" +
            "FROM Hit h JOIN App a ON a.id=h.app.id " +
            "WHERE h.dateTime BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY h.uri, h.ip " +
            "ORDER BY COUNT(h.id) DESC")
    List<Stats> findAllUniqueHits(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT new  ru.practicum.ewm.dto.Stats(a.name, h.uri, count (h.id))" +
            "FROM Hit h JOIN App a ON a.id=h.app.id " +
            "WHERE (h.dateTime BETWEEN :startDateTime AND :endDateTime) AND (h.uri IN (:uris))" +
            "GROUP BY h.uri,a.name " +
            "ORDER BY COUNT(h.id) DESC")
    List<Stats> findHitsByUris(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.dto.Stats(a.name, h.uri, count (h.id))" +
            "FROM Hit h JOIN App a ON a.id=h.app.id " +
            "WHERE h.dateTime BETWEEN :startDateTime AND :endDateTime AND (h.uri IN (:uris))" +
            "GROUP BY h.uri, h.ip " +
            "ORDER BY COUNT (h.id) desc")
    List<Stats> findUniqueHitsByUris(LocalDateTime startDateTime, LocalDateTime endDateTime, List<String> uris);
}
