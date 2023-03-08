package ru.practicum.ewm.main_service.participate_request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.exception.model.ConflictException;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;
import ru.practicum.ewm.main_service.participate_request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main_service.participate_request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main_service.participate_request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main_service.participate_request.dto.RequestMapper;
import ru.practicum.ewm.main_service.participate_request.model.ParticipationRequest;
import ru.practicum.ewm.main_service.participate_request.repository.RequestRepository;
import ru.practicum.ewm.main_service.participate_request.util.RequestStatus;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id" + eventId + " not exists in the DB."));


        if (event.getOwner().getId() != userId) {
            throw new NotFoundException("User with id" + userId + " is not owner for event with id" + eventId);
        }

        return requestRepository.findByEvent(event).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id" + eventId + " not exists in the DB."));


        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        Set<ParticipationRequestDto> confirmed = new HashSet<>();
        Set<ParticipationRequestDto> rejected = new HashSet<>();
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(confirmed, rejected);
        List<ParticipationRequest> pendingRequests = requests.stream()
                .filter(request1 -> request1.getStatus().equals(RequestStatus.PENDING))
                .collect(Collectors.toList());

        if (pendingRequests.size()==0) {
            throw new ConflictException("Nothing to update");
        }

        long confirmedRequestsCount = (long) requestRepository.findAllByEventAndStatus(event, RequestStatus.CONFIRMED).size();

        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (!event.isNeededModeration() || event.getParticipantLimit() == 0) {
            for (var req : requests) {
                req.setStatus(RequestStatus.CONFIRMED);
            }
            result.getConfirmedRequests().addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);

            return result;
        }

        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if ((confirmedRequestsCount + request.getRequestIds().size()) > event.getParticipantLimit()) {
            throw new ConflictException("The participant limit for event with id " + eventId + " has been reached");
        }

        if ((confirmedRequestsCount + request.getRequestIds().size()) == event.getParticipantLimit() &&
                request.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (ParticipationRequest req : requests) {
                req.setStatus(RequestStatus.REJECTED);
            }
            confirmed.addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setConfirmedRequests(confirmed);

            List<ParticipationRequest> otherPendingRequests = requestRepository.findAllByEventAndStatus(event,
                    RequestStatus.PENDING);
            for (ParticipationRequest req : otherPendingRequests) {
                req.setStatus(RequestStatus.REJECTED);
            }
            requestRepository.saveAll(otherPendingRequests);
            rejected.addAll(otherPendingRequests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            result.setRejectedRequests(rejected);
            return result;
        }

        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (ParticipationRequest req : requests) {
                req.setStatus(RequestStatus.CONFIRMED);
            }
            confirmed.addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setConfirmedRequests(confirmed);
        } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
            for (ParticipationRequest req : requests) {
                req.setStatus(RequestStatus.REJECTED);
            }
            rejected.addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setRejectedRequests(rejected);
        }

        return result;
    }


    public List<ParticipationRequestDto> getByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB."));

        return requestRepository.findAllByRequester(user).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }


    public ParticipationRequestDto create(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id" + eventId + " not exists in the DB."));

        //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (user.getId() == event.getOwner().getId()) {
            throw new ConflictException("User with id " + userId + " is owner for event with id" + eventId);
        }

        //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (event.getPublishedDateTime() == null) {
            throw new ConflictException("Event with id= " + eventId + " is not published yet");
        }

        long confirmedRequestsCount = requestRepository.findAllByEventAndStatus(event, RequestStatus.CONFIRMED).size();

        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        long requestCount = requestRepository.findByEvent(event).size();
        if (event.getParticipantLimit() == confirmedRequestsCount) {
            throw new ConflictException(
                    "The participant limit foe event with id " + event.getId() + " has been reached");
        }
        //нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.findAllByEventAndRequester(event, user).size() > 0) {
            throw new ConflictException("Request for participate in event with id "+event.getId()+
                    " already is exist for user " + user.getId());
        }
        ParticipationRequest request = new ParticipationRequest(null,
                LocalDateTime.now().withNano(0),
                event,
                user,
                RequestStatus.PENDING);
        //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        if (!event.isNeededModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.toRequestDto(requestRepository.save(request));

    }

    public ParticipationRequestDto cancelRequestByUser(Long userId, Long requestId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB"));

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not exists in the DB"));

        if (request.getRequester().getId() != user.getId()) {
            throw new ConflictException("User with id" + userId + " is not owner for request with id" + requestId);
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}
