package ru.practicum.ewm.main_service.participate_request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main_service.participate_request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main_service.participate_request.service.ParticipateRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class PrivateController {

    private final ParticipateRequestService requestService;

    @Autowired
    public PrivateController(ParticipateRequestService requestService) {
        this.requestService = requestService;
    }

    //requests
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserEvents(@PathVariable Long userId) {

        return requestService.getByUserId(userId);
    }

    //нельзя добавить повторный запрос (Ожидается код ошибки 409)
    //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
    //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
    //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
    //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam long eventId) {
        return requestService.create(userId, eventId);
    }

    //отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelByUser(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequestByUser(userId, requestId);
    }
}
