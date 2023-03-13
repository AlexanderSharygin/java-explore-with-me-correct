package ru.practicum.ewm.main_service.event_comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event_comment.util.CommentState;
import ru.practicum.ewm.main_service.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_comments")
public class EventComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank
    @Size(min = 5, max = 1000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "event_id")
    @NotNull
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id")
    @NotNull
    private User user;

    @Column (name = "create_date_time")
    private LocalDateTime createdDateTime;

    @Enumerated(EnumType.STRING)
    private CommentState state;

    @Column (name = "is_published")
    private Boolean isPublished;

    @Column (name = "publish_date_time")
    private LocalDateTime publishedDateTime;
}