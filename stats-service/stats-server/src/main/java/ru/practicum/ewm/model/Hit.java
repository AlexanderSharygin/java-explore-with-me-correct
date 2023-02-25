package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private App app;

    @NotBlank
    @Size(min = 1, max = 200)
    private String uri;

    @NotBlank
    @Size(min = 1, max = 64)
    private String ip;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;
}
