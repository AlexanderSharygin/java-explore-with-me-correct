package ru.practicum.ewm.main_service.event_comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentDto;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentUpdateRequest;
import ru.practicum.ewm.main_service.event_comment.service.EventCommentsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
public class PrivateCommentsController {

    private final EventCommentsService commentService;

    @Autowired
    public PrivateCommentsController(EventCommentsService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/users/{userId}/events/{eventId}/comments")
    public List<EventCommentDto> getAllCommentsForEvent(
            @PathVariable long userId, @PathVariable long eventId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        Pageable paging = PageRequest.of(from, size);
        return commentService.getCommentsForEvent(userId, eventId, paging);
    }

    @GetMapping("/users/{userId}/comments")
    public List<EventCommentDto> getAllCommentsForUser(
            @PathVariable long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        Pageable paging = PageRequest.of(from, size);
        return commentService.getAllCommentsByUserId(userId, paging);
    }

    @GetMapping("/users/{userId}/comments/{commentId}")
    public EventCommentDto getCommentByIdForUser(@PathVariable long userId, @PathVariable long commentId) {
        return commentService.getCommentByIdForAuthor(userId, commentId);
    }

    @PostMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public EventCommentDto createComment(
            @PathVariable long userId,
            @RequestParam(name = "eventId") @Positive long eventId,
            @Valid @RequestBody EventCommentDto eventCommentDto) {
        return commentService.createComment(userId, eventId, eventCommentDto);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public EventCommentDto updateCommentByUser(
            @PathVariable long userId,
            @PathVariable long commentId,
            @Valid @RequestBody EventCommentUpdateRequest commentDto) {
        return commentService.updateCommentByAuthor(userId, commentId, commentDto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(
            @PathVariable long userId,
            @PathVariable long commentId) {
        commentService.deleteCommentByAuthor(userId, commentId);
    }
}