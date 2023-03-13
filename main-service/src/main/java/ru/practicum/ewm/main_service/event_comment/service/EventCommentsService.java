package ru.practicum.ewm.main_service.event_comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.event.util.EventState;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentDto;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentPublicInfo;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentUpdateRequest;
import ru.practicum.ewm.main_service.event_comment.mapper.EventCommentMapper;
import ru.practicum.ewm.main_service.event_comment.model.EventComment;
import ru.practicum.ewm.main_service.event_comment.repository.EventCommentRepository;
import ru.practicum.ewm.main_service.event_comment.util.CommentState;
import ru.practicum.ewm.main_service.event_comment.util.EventCommentAdminAction;
import ru.practicum.ewm.main_service.exception.model.BadRequestException;
import ru.practicum.ewm.main_service.exception.model.ConflictException;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;
import ru.practicum.ewm.main_service.location.model.Location;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventCommentsService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final EventCommentRepository commentRepository;


    @Autowired
    public EventCommentsService(UserRepository userRepository, EventRepository eventRepository, EventCommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;

    }

    public List<EventCommentPublicInfo> getAllCommentsInfoForPublicEvent(long eventId, Pageable pageable) {
        Event event = getEventIfExist(eventId);
        checkIfEventPublished(event);
        List<EventComment> comments = commentRepository.findByEvent_IdAndState(eventId, CommentState.APPROVED, pageable);

        return comments.stream()
                .map(EventCommentMapper::toPublicInfoFromEventComment)
                .collect(Collectors.toList());
    }

    public List<EventCommentDto> getAllCommentsByUserId(long userId, Pageable pageable) {
        getUserIfExist(userId);
        List<EventComment> comments = commentRepository.findByUser_Id(userId, pageable);

        return comments.stream()
                .map(EventCommentMapper::toDtoFromEventComment)
                .collect(Collectors.toList());
    }

    public List<EventCommentDto> getCommentsForEvent(long userId, long eventId, Pageable pageable) {
        User user = getUserIfExist(userId);
        Event event = getEventIfExist(eventId);
        if (!Objects.equals(event.getOwner().getId(), user.getId())) {
            throw new NotFoundException("User with id" + userId + " is not owner for event with id" + eventId);
        }
        List<EventComment> comments = commentRepository.findByEvent_Id(eventId, pageable);

        return comments.stream()
                .map(EventCommentMapper::toDtoFromEventComment)
                .collect(Collectors.toList());
    }

    public EventCommentDto getCommentByIdForAuthor(long userId, long comId) {
        User user = getUserIfExist(userId);
        EventComment comment = getCommentIfExist(comId);
        checkIfUserIsAuthor(comment, user);

        return EventCommentMapper.toDtoFromEventComment(comment);
    }

    public EventCommentDto createComment(long userId, long eventId, EventCommentDto eventCommentDto) {
        User user = getUserIfExist(userId);
        Event event = getEventIfExist(eventId);
        eventCommentDto.setId(-1L);
        eventCommentDto.setUserId(userId);
        eventCommentDto.setEventId(eventId);
        eventCommentDto.setState(CommentState.NEW);
        eventCommentDto.setCreatedOn(LocalDateTime.now());
        eventCommentDto.setIsPublished(false);
        checkIfEventPublished(event);
        EventComment comment = EventCommentMapper.toEventCommentFromShortDto(eventCommentDto, event, user);

        EventComment eventComment = commentRepository.save(comment);

        return EventCommentMapper.toDtoFromEventComment(eventComment);
    }

    public EventCommentDto updateCommentByAuthor(long userId, long commentId, EventCommentUpdateRequest commentDto) {
        User owner = getUserIfExist(userId);
        EventComment comment = getCommentIfExist(commentId);
        checkIfUserIsAuthor(comment, owner);
        comment.setText(commentDto.getText());
        comment.setState(CommentState.NEW);
        commentRepository.save(comment);

        return EventCommentMapper.toDtoFromEventComment(getCommentIfExist(commentId));
    }

    public EventCommentDto updateCommentByAdmin(long commentId, EventCommentUpdateRequest commentDto) {
        EventComment comment = getCommentIfExist(commentId);
        if (commentDto.getAction() == null) {
            throw new BadRequestException("Admin action could not be empty or null");
        } else {
            if (commentDto.getAction().equals(EventCommentAdminAction.ACCEPT)) {
                comment.setPublishedDateTime(LocalDateTime.now());
                comment.setIsPublished(true);
                comment.setState(CommentState.APPROVED);
            }
            else if (commentDto.getAction().equals(EventCommentAdminAction.REJECT)) {
                comment.setPublishedDateTime(null);
                comment.setIsPublished(false);
                comment.setState(CommentState.REJECTED);
            } else {
                throw new BadRequestException("Unknown state value. Action value could be ACCEPT or REJECT");
            }
        }
        commentRepository.save(comment);

        return EventCommentMapper.toDtoFromEventComment(getCommentIfExist(commentId));
    }

    public void deleteCommentByAuthor(long userId, long commentId) {
        User owner = getUserIfExist(userId);
        EventComment comment = getCommentIfExist(commentId);
        checkIfUserIsAuthor(comment, owner);
        commentRepository.delete(comment);
    }

    public void deleteCommentByAdmin(long commentId) {
        EventComment comment = getCommentIfExist(commentId);
        commentRepository.delete(comment);
    }

    private User getUserIfExist(long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not exists in the DB"));
    }

    private Event getEventIfExist(long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not exist in the DB"));
    }

    private EventComment getCommentIfExist(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id " + commentId + " not exist in the DB"));
    }

    private void checkIfEventPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published yet");
        }
    }

    private void checkIfUserIsAuthor(EventComment comment, User user) {
        if (!Objects.equals(user.getId(), comment.getUser().getId())) {
            throw new ConflictException(
                    "User with id" + user.getId() + " is not author for comment with id " + comment.getId());
        }
    }
}