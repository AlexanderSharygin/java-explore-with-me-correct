package ru.practicum.ewm.main_service.event_category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event_category.dto.CategoryDto;
import ru.practicum.ewm.main_service.event_category.service.EventCategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
public class EventCategoryPublicController {

    private final EventCategoryService categoryService;

    @Autowired
    public EventCategoryPublicController(EventCategoryService categoryService) {
        this.categoryService = categoryService;
    }


    //Categories

    @GetMapping
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                           @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(from, size);
        return categoryService.getAll(paging);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable long catId) {
        return categoryService.getById(catId);
    }
}
