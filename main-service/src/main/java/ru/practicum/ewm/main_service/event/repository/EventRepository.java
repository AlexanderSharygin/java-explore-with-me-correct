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
            "JOIN ParticipationRequest r on e.id = r.event.id " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in :categoriesIds)" +
            "and e.startDateTime between :startDateTime and :endDateTime " +
            "and e.state = :eventState " +
            "and r.status = :requestState " +
            "and e.isPaid = :isPaid " +
            "GROUP BY e.id, e.annotation, e.category, e.createdDateTime, e.description, e.startDateTime," +
            " e.owner, e.location, e.isPaid, e.participantLimit, e.publishedDateTime, e.isNeededModeration," +
            " e.state, e.title " +
            "HAVING count(r.status) < e.participantLimit ")
    Page<Event> findAllAvailablePublishedEventsByCategoryAndStateBetweenDates(String text, LocalDateTime startDateTime,
                                                                              LocalDateTime endDateTime,
                                                                              List<Long> categoriesIds,
                                                                              Pageable pageable, EventState eventState,
                                                                              RequestStatus requestState, boolean isPaid);

    @Query(" SELECT e " +
            "FROM Event e " +
            "JOIN ParticipationRequest r on e.id = r.event.id " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in :categoriesIds)" +
            "and e.startDateTime   >= :startDateTime " +
            "and e.state = :eventState " +
            "and r.status = :requestState " +
            "and e.isPaid = :isPaid " +
            "GROUP BY e.id, e.annotation, e.category, e.createdDateTime, e.description, e.startDateTime," +
            " e.owner, e.location, e.isPaid, e.participantLimit, e.publishedDateTime, e.isNeededModeration," +
            " e.state, e.title " +
            "HAVING count(r.status) < e.participantLimit ")
    Page<Event> findAllAvailablePublishedEventsByCategoryAndStateAfterDate(String text, LocalDateTime startDateTime,
                                                                           List<Long> categoriesIds, Pageable pageable,
                                                                           EventState eventState, RequestStatus requestState,
                                                                           boolean isPaid);


    @Query(" select e from Event e " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in :categoriesIds)" +
            "and e.startDateTime between :startDateTime and :endDateTime " +
            "and e.isPaid = :isPaid " +
            "and e.state = :state")
    Page<Event> findAllEventsWithStatusBetweenDates(String text, LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                    List<Long> categoriesIds, EventState state,
                                                    Pageable pageable, boolean isPaid);


    @Query(" select e from Event e " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in :categoriesIds)" +
            "and e.isPaid = :isPaid " +
            "and e.startDateTime >= :startDateTime " +
            "and e.state = :state")
    Page<Event> findAllEventsWithStatusAfterDate(String text, LocalDateTime startDateTime,
                                                 List<Long> categoriesIds, EventState state, Pageable pageable, boolean isPaid);


    //List<Long> usersIds, List<String> states, List<Long> categoriesIds, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size
    @Query("select e from Event e " +
            "where e.owner.id in :usersIds " +
            "and e.state = :states" +
            " and e.category.id = :categoriesIds " +
            "and e.startDateTime between :startDateTime and :endDateTime")
    Page<Event> findAllEventsBetweenDatesForUsersByStateAndCategories(List<Long> usersIds, List<EventState> states,
                                                                      List<Long> categoriesIds, LocalDateTime startDateTime,
                                                                      LocalDateTime endDateTime, Pageable pageable);

    @Query("select e from Event e " +
            "where e.owner.id in :usersIds " +
            "and e.state = :states" +
            " and e.category.id = :categoriesIds " +
            "and e.startDateTime > :dateTime")
    Page<Event> findAllEventsAfterDateForUsersByStateAndCategories(List<Long> usersIds, List<EventState> states,
                                                                   List<Long> categoriesIds,
                                                                   LocalDateTime dateTime,
                                                                   Pageable pageable);
}
