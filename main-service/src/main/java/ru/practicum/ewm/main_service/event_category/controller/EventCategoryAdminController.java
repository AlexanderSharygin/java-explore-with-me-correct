package ru.practicum.ewm.main_service.event_category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event_category.dto.CategoryDto;
import ru.practicum.ewm.main_service.event_category.service.EventCategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
public class EventCategoryAdminController {
    private final EventCategoryService categoryService;

    @Autowired
    public EventCategoryAdminController(EventCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@RequestBody @Valid CategoryDto categoryDto,
                              @PathVariable Long catId) {
        return categoryService.update(catId, categoryDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryDto categoryDto) {
        categoryDto.setId(-1L);
        return categoryService.create(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    }
}

