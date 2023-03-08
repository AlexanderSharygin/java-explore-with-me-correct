package ru.practicum.ewm.main_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserShortDto {
    private long id;
    private String name;
}
