package ru.practicum.ewm.main_service.event_comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main_service.event_comment.model.EventComment;
import ru.practicum.ewm.main_service.event_comment.util.CommentState;

import java.util.List;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    List<EventComment> findByEvent_Id(Long id, Pageable pageable);
    List<EventComment> findByUser_Id(Long id, Pageable pageable);
    List<EventComment> findByEvent_IdAndState(Long id, CommentState state, Pageable pageable);
}
