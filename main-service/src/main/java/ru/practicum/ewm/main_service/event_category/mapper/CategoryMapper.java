package ru.practicum.ewm.main_service.event_category.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.event_category.dto.CategoryDto;
import ru.practicum.ewm.main_service.event_category.dto.NewCategoryDto;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryMapper {

    public static CategoryDto toCategoryDto(EventCategory category) {
        if (category == null) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        return categoryDto;
    }

    public static EventCategory toCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        EventCategory category = new EventCategory();

        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }

    public static EventCategory toCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            return null;
        }

        EventCategory category = new EventCategory();

        category.setId(null);
        category.setName(newCategoryDto.getName());

        return category;
    }
}
