package ru.practicum.ewm.main_service.location.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LocationDto {
    private Double lat;
    private Double lon;
}
