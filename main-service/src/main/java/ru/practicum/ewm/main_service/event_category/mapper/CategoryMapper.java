package ru.practicum.ewm.main_service.event_category.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.event_category.dto.EventCategoryDto;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static EventCategoryDto toCategoryDtoFromCategory(EventCategory category) {
        return new EventCategoryDto(category.getId(), category.getName());
    }

    public static EventCategory toCategoryFromCategoryDto(EventCategoryDto eventCategoryDto) {
        return new EventCategory(eventCategoryDto.getId(), eventCategoryDto.getName());
    }
}