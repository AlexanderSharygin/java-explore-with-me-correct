package ru.practicum.ewm.main_service.event_category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main_service.event.model.Event;
import ru.practicum.ewm.main_service.event.repository.EventRepository;
import ru.practicum.ewm.main_service.event_category.dto.CategoryDto;
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

    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getById(long catId) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " not exists in the DB")));
    }

    public CategoryDto update(long catId, CategoryDto categoryDto) {

        EventCategory categoryToUpdate = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " not exists in the DB"));

        Optional<EventCategory> categoryWithSameName = categoryRepository.findByName(categoryDto.getName());
        if (categoryWithSameName.isPresent()) {
            throw new ConflictException("Category with name " + categoryDto.getName() + " already exists in the DB");
        }


        categoryToUpdate.setName(categoryDto.getName());

        return CategoryMapper.toCategoryDto(categoryToUpdate);
    }

    public CategoryDto create(CategoryDto categoryDto) {
        Optional<EventCategory> categoryWithSameName = categoryRepository.findByName(categoryDto.getName());
        if (categoryWithSameName.isPresent()) {
            throw new ConflictException("Category with name " + categoryDto.getName() + " already exists in the DB");
        }

        EventCategory category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    public void delete(long catId) {
        EventCategory category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " not exists in the DB"));
        List<Event> eventsList = eventRepository.findByCategory(category);

        if (!eventsList.isEmpty()) {
            throw new ConflictException("Can't be removed - category is not empty");
        }
        categoryRepository.delete(category);
    }
}
