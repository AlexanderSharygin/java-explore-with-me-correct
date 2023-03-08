package ru.practicum.ewm.main_service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CompilationRequest {
    private Set<Long> events;
    @NotNull
    private Boolean pinned;
    private String title;
}
