package ru.practicum.ewm.main_service.event_category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.event_category.dto.EventCategoryDto;
import ru.practicum.ewm.main_service.event_category.service.EventCategoryService;

import javax.validation.Valid;

@RestController
public class EventCategoryAdminController {
    private final EventCategoryService categoryService;

    @Autowired
    public EventCategoryAdminController(EventCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PatchMapping("/admin/categories/{catId}")
    public EventCategoryDto update(@RequestBody @Valid EventCategoryDto eventCategoryDto, @PathVariable Long catId) {
        return categoryService.update(catId, eventCategoryDto);
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public EventCategoryDto create(@RequestBody @Valid EventCategoryDto eventCategoryDto) {
        eventCategoryDto.setId(-1L);
        return categoryService.create(eventCategoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        categoryService.delete(catId);
    }
}