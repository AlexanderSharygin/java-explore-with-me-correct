package ru.practicum.ewm.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.model.Hit;


@NoArgsConstructor
public class HitMapper {
    public static HitDto toHitDto(Hit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp().getName(),
                hit.getUri(),
                hit.getIp(),
                hit.getDateTime()
        );
    }

    public static Hit toHit(HitDto hitDto) {
        return new Hit(
                hitDto.getId(),
                null,
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getTimestamp()
        );
    }
}
