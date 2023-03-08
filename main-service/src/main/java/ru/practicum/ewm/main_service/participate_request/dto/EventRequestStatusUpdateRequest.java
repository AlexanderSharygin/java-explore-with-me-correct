package ru.practicum.ewm.main_service.participate_request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.main_service.participate_request.util.RequestStatus;


import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
