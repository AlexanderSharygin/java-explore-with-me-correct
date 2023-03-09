package ru.practicum.ewm.main_service.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.util.EventState;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;
import ru.practicum.ewm.main_service.participate_request.util.RequestStatus;
import ru.practicum.ewm.main_service.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory(EventCategory category);

    Page<Event> findAllByOwner(User owner, Pageable pageable);

    @Query(" SELECT e " +
            "FROM Event e " +
            "JOIN ParticipationRequest r ON e.id = r.event.id " +
            "WHERE (UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(e.description)  LIKE UPPER(concat('%', :text, '%')) " +
            "OR :text is null) " +
            "AND (e.category.id IN :categoriesIds)" +
            "AND e.startDateTime BETWEEN :startDateTime AND :endDateTime " +
            "AND e.state = :eventState " +
            "AND r.status = :requestState " +
            "AND e.isPaid = :isPaid " +
            "GROUP BY e.id, e.annotation, e.category, e.createdDateTime, e.description, e.startDateTime," +
            " e.owner, e.location, e.isPaid, e.participantLimit, e.publishedDateTime, e.isNeededModeration," +
            " e.state, e.title " +
            "HAVING COUNT(r.status) < e.participantLimit ")
    Page<Event> findAllAvailablePublishedEventsByCategoryAndStateBetweenDates(String text, LocalDateTime startDateTime,
                                                                              LocalDateTime endDateTime,
                                                                              List<Long> categoriesIds,
                                                                              Pageable pageable, EventState eventState,
                                                                              RequestStatus requestState, boolean isPaid);

    @Query(" SELECT e " +
            "FROM Event e " +
            "JOIN ParticipationRequest r ON e.id = r.event.id " +
            "WHERE (upper(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR :text is null) " +
            "AND (e.category.id IN :categoriesIds)" +
            "AND e.startDateTime   >= :startDateTime " +
            "AND e.state = :eventState " +
            "AND r.status = :requestState " +
            "AND e.isPaid = :isPaid " +
            "GROUP BY e.id, e.annotation, e.category, e.createdDateTime, e.description, e.startDateTime," +
            " e.owner, e.location, e.isPaid, e.participantLimit, e.publishedDateTime, e.isNeededModeration," +
            " e.state, e.title " +
            "HAVING COUNT(r.status) < e.participantLimit ")
    Page<Event> findAllAvailablePublishedEventsByCategoryAndStateAfterDate(String text, LocalDateTime startDateTime,
                                                                           List<Long> categoriesIds, Pageable pageable,
                                                                           EventState eventState,
                                                                           RequestStatus requestState,
                                                                           boolean isPaid);

    @Query(" SELECT e FROM Event e " +
            "WHERE (UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR :text is null) " +
            "AND (e.category.id IN :categoriesIds)" +
            "AND e.startDateTime BETWEEN :startDateTime AND :endDateTime " +
            "AND e.isPaid = :isPaid " +
            "AND e.state = :state")
    Page<Event> findAllEventsWithStatusBetweenDates(String text, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                    List<Long> categoriesIds, EventState state,
                                                    Pageable pageable, boolean isPaid);


    @Query(" SELECT e FROM Event e " +
            "WHERE (UPPER(e.annotation) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(e.description) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR :text is null) " +
            "AND (e.category.id IN :categoriesIds)" +
            "AND e.isPaid = :isPaid " +
            "AND e.startDateTime >= :startDateTime " +
            "AND e.state = :state")
    Page<Event> findAllEventsWithStatusAfterDate(String text, LocalDateTime startDateTime,
                                                 List<Long> categoriesIds, EventState state,
                                                 Pageable pageable, boolean isPaid);

    @Query("SELECT e FROM Event e " +
            "WHERE e.owner.id IN :usersIds " +
            "AND e.state = :states " +
            "AND e.category.id = :categoriesIds " +
            "AND e.startDateTime BETWEEN :startDateTime AND :endDateTime")
    Page<Event> findAllEventsBetweenDatesForUsersByStateAndCategories(List<Long> usersIds, List<EventState> states,
                                                                      List<Long> categoriesIds,
                                                                      LocalDateTime startDateTime,
                                                                      LocalDateTime endDateTime, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.owner.id IN :usersIds " +
            "AND e.state = :states " +
            "AND e.category.id = :categoriesIds " +
            "AND e.startDateTime > :dateTime")
    Page<Event> findAllEventsAfterDateForUsersByStateAndCategories(List<Long> usersIds, List<EventState> states,
                                                                   List<Long> categoriesIds,
                                                                   LocalDateTime dateTime,
                                                                   Pageable pageable);
}
