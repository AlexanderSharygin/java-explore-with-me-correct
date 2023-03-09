package ru.practicum.ewm.main_service.event_category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.event_category.dto.EventCategoryDto;
import ru.practicum.ewm.main_service.event_category.mapper.CategoryMapper;
import ru.practicum.ewm.main_service.event_category.model.EventCategory;
import ru.practicum.ewm.main_service.event_category.repository.CategoryRepository;
import ru.practicum.ewm.main_service.exception.model.ConflictException;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventCategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public List<EventCategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDtoFromCategory)
                .collect(Collectors.toList());
    }

    public EventCategoryDto getById(long catId) {
        EventCategory category = getCategoryIfExist(catId);

        return CategoryMapper.toCategoryDtoFromCategory(category);
    }

    public EventCategoryDto update(long catId, EventCategoryDto eventCategoryDto) {
        EventCategory categoryToUpdate = getCategoryIfExist(catId);
        Optional<EventCategory> categoryWithSameName = categoryRepository.findByName(eventCategoryDto.getName());
        if (categoryWithSameName.isPresent()) {
            throw new ConflictException(
                    "Category with name " + eventCategoryDto.getName() + " already exists in the DB");
        }
        categoryToUpdate.setName(eventCategoryDto.getName());
        EventCategory updatedCategory = categoryRepository.save(categoryToUpdate);

        return CategoryMapper.toCategoryDtoFromCategory(updatedCategory);
    }

    public EventCategoryDto create(EventCategoryDto eventCategoryDto) {
        Optional<EventCategory> categoryWithSameName = categoryRepository.findByName(eventCategoryDto.getName());
        if (categoryWithSameName.isPresent()) {
            throw new ConflictException(
                    "Category with name " + eventCategoryDto.getName() + " already exists in the DB");
        }

        EventCategory category = categoryRepository.save(CategoryMapper.toCategoryFromCategoryDto(eventCategoryDto));
        return CategoryMapper.toCategoryDtoFromCategory(category);
    }

    public void delete(long catId) {
        EventCategory category = getCategoryIfExist(catId);
        List<Event> eventsList = eventRepository.findByCategory(category);
        if (!eventsList.isEmpty()) {
            throw new ConflictException("Can't be removed - category is not empty");
        }
        categoryRepository.delete(category);
    }

    private EventCategory getCategoryIfExist(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " not exists in the DB"));
    }
}
