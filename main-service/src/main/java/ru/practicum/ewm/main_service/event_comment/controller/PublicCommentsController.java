package ru.practicum.ewm.main_service.event_comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentPublicInfo;
import ru.practicum.ewm.main_service.event_comment.service.EventCommentsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping
public class PublicCommentsController {

    private final EventCommentsService commentService;

    @Autowired
    public PublicCommentsController(EventCommentsService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/events/{eventId}/comments")
    public List<EventCommentPublicInfo> getAllEventsCommentsForPublic(
            @PathVariable long eventId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        Pageable paging = PageRequest.of(from, size);
        return commentService.getAllCommentsInfoForPublicEvent(eventId, paging);
    }
}
