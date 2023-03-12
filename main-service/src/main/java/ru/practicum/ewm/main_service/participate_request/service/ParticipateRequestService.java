package ru.practicum.ewm.main_service.participate_request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main_service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main_service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.exception.model.ConflictException;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;
import ru.practicum.ewm.main_service.participate_request.mapper.RequestMapper;
import ru.practicum.ewm.main_service.participate_request.model.ParticipationRequest;
import ru.practicum.ewm.main_service.participate_request.repository.RequestRepository;
import ru.practicum.ewm.main_service.participate_request.util.RequestStatus;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParticipateRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ParticipateRequestService(RequestRepository requestRepository, UserRepository userRepository,
                                     EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        getUserIfExist(userId);
        Event event = getEventIfExist(eventId);
        if (!Objects.equals(event.getOwner().getId(), userId)) {
            throw new NotFoundException("User with id" + userId + " is not owner for event with id" + eventId);
        }
        List<ParticipationRequest> result = requestRepository.findByEvent(event);

        return result.stream()
                .map(RequestMapper::fromRequestTpRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest request) {

        getUserIfExist(userId);
        Event event = getEventIfExist(eventId);
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        Set<ParticipationRequestDto> confirmed = new HashSet<>();
        Set<ParticipationRequestDto> rejected = new HashSet<>();
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(confirmed, rejected);
        List<ParticipationRequest> pendingRequests = requests.stream()
                .filter(request1 -> request1.getStatus().equals(RequestStatus.PENDING))
                .collect(Collectors.toList());

        if (pendingRequests.size() == 0) {
            throw new ConflictException("Nothing to update");
        }
        long confirmedRequestsCount = requestRepository.findAllByEventAndStatus(event, RequestStatus.CONFIRMED).size();
        if (!event.isNeededModeration() || event.getParticipantLimit() == 0) {
            requests.forEach(req -> req.setStatus(RequestStatus.CONFIRMED));
            result.getConfirmedRequests().addAll(requests.stream()
                    .map(RequestMapper::fromRequestTpRequestDto)
                    .collect(Collectors.toList()));
            requestRepository.saveAll(requests);

            return result;
        }
        if ((confirmedRequestsCount + request.getRequestIds().size()) > event.getParticipantLimit()) {
            throw new ConflictException("The participant limit for event with id " + eventId + " has been reached");
        }

        if ((confirmedRequestsCount + request.getRequestIds().size()) == event.getParticipantLimit() &&
                request.getStatus().equals(RequestStatus.CONFIRMED)) {
            requests.forEach(req -> req.setStatus(RequestStatus.REJECTED));
            confirmed.addAll(requests.stream()
                    .map(RequestMapper::fromRequestTpRequestDto)
                    .collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setConfirmedRequests(confirmed);

            List<ParticipationRequest> otherPendingRequests = requestRepository
                    .findAllByEventAndStatus(event, RequestStatus.PENDING);
            otherPendingRequests.forEach(req -> req.setStatus(RequestStatus.REJECTED));
            requestRepository.saveAll(otherPendingRequests);
            rejected.addAll(otherPendingRequests.stream()
                    .map(RequestMapper::fromRequestTpRequestDto)
                    .collect(Collectors.toList()));
            result.setRejectedRequests(rejected);

            return result;
        }

        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            requests.forEach(req -> req.setStatus(RequestStatus.CONFIRMED));
            confirmed.addAll(requests.stream().map(RequestMapper::fromRequestTpRequestDto)
                    .collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setConfirmedRequests(confirmed);
        } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
            requests.forEach(req -> req.setStatus(RequestStatus.REJECTED));
            rejected.addAll(requests.stream().map(RequestMapper::fromRequestTpRequestDto)
                    .collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setRejectedRequests(rejected);
        }

        return result;
    }

    public List<ParticipationRequestDto> getByUserId(Long userId) {
        User user = getUserIfExist(userId);
        List<ParticipationRequest> result = requestRepository.findAllByRequester(user);

        return result.stream()
                .map(RequestMapper::fromRequestTpRequestDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = getUserIfExist(userId);
        Event event = getEventIfExist(eventId);
        if (Objects.equals(user.getId(), event.getOwner().getId())) {
            throw new ConflictException("User with id " + userId + " is owner for event with id" + eventId);
        }
        if (event.getPublishedDateTime() == null) {
            throw new ConflictException("Event with id " + eventId + " is not published yet");
        }

        long confirmedRequestsCount = requestRepository.findAllByEventAndStatus(event, RequestStatus.CONFIRMED).size();
        if (confirmedRequestsCount == event.getParticipantLimit()) {
            throw new ConflictException(
                    "The participant limit foe event with id " + event.getId() + " has been reached");
        }
        if (requestRepository.findAllByEventAndRequester(event, user).size() > 0) {
            throw new ConflictException("Request for participate in event with id " + event.getId() +
                    " already is exist for user " + user.getId());
        }
        ParticipationRequest request = new ParticipationRequest(null, LocalDateTime.now(), event, user,
                RequestStatus.PENDING);
        if (!event.isNeededModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        ParticipationRequest result = requestRepository.save(request);

        return RequestMapper.fromRequestTpRequestDto(result);
    }

    public ParticipationRequestDto cancelRequestByUser(Long userId, Long requestId) {
        User user = getUserIfExist(userId);
        ParticipationRequest request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not exists in the DB"));
        if (!Objects.equals(request.getRequester().getId(), user.getId())) {
            throw new ConflictException("User with id" + userId + " is not owner for request with id" + requestId);
        }
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest result = requestRepository.save(request);

        return RequestMapper.fromRequestTpRequestDto(result);
    }

    private User getUserIfExist(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB"));
    }

    private Event getEventIfExist(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id" + eventId + " not exists in the DB."));
    }
}
