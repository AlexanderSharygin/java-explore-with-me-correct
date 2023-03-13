package ru.practicum.ewm.main_service.event_comment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentDto;
import ru.practicum.ewm.main_service.event_comment.dto.EventCommentUpdateRequest;
import ru.practicum.ewm.main_service.event_comment.service.EventCommentsService;

import javax.validation.Valid;

@RestController
public class AdminCommentsController {

    private final EventCommentsService commentService;

    @Autowired
    public AdminCommentsController(EventCommentsService commentService) {
        this.commentService = commentService;
    }



    @PatchMapping("/admin/comments/{commentId}")
    public EventCommentDto updateCommentByAdmin(@PathVariable long commentId,
                                                @Valid @RequestBody EventCommentUpdateRequest commentDto) {
        return commentService.updateCommentByAdmin(commentId, commentDto);
    }

    @DeleteMapping("/admin/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable long comId) {
        commentService.deleteCommentByAdmin(comId);
    }
}
