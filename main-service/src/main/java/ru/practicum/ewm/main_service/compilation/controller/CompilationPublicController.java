package ru.practicum.ewm.main_service.compilation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.compilation.dto.CompilationDto;
import ru.practicum.ewm.main_service.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
public class CompilationPublicController {

    private final CompilationService compilationService;

    @Autowired
    public CompilationPublicController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam(value = "pinned", defaultValue = "false") boolean pinned,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(from, size);
        return compilationService.getAll(pinned, paging);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable long compId) {
        return compilationService.getById(compId);
    }
}
