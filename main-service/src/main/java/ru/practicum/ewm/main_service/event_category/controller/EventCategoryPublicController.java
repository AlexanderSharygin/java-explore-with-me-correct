package ru.practicum.ewm.main_service.event_category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main_service.event_category.dto.EventCategoryDto;
import ru.practicum.ewm.main_service.event_category.service.EventCategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
public class EventCategoryPublicController {

    private final EventCategoryService categoryService;

    @Autowired
    public EventCategoryPublicController(EventCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public List<EventCategoryDto> getCategories(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(from, size);
        return categoryService.getAll(paging);
    }

    @GetMapping("/categories/{catId}")
    public EventCategoryDto getCategory(@PathVariable long catId) {
        return categoryService.getById(catId);
    }
}