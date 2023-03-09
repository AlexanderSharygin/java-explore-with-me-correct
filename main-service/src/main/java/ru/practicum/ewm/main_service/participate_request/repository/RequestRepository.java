package ru.practicum.ewm.main_service.participate_request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.participate_request.model.ParticipationRequest;
import ru.practicum.ewm.main_service.participate_request.util.RequestStatus;
import ru.practicum.ewm.main_service.user.model.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByEvent(Event event);

    List<ParticipationRequest> findAllByRequester(User requester);

    List<ParticipationRequest> findAllByEventAndRequester(Event event, User requester);

    List<ParticipationRequest> findAllByEventAndStatus(Event event, RequestStatus status);

    @Query("SELECT p " +
            "FROM ParticipationRequest p " +
            "WHERE p.event IN :events AND p.status = :status")
    List<ParticipationRequest> findAllByEventInAndStatus(List<Event> events, RequestStatus status);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);
}
