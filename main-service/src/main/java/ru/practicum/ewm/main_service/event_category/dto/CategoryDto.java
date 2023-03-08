package ru.practicum.ewm.main_service.event_category.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryDto {
    private long id;
    @NotBlank
    private String name;

}
