package ru.practicum.ewm.main_service.participate_request.dto;

import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.participate_request.model.ParticipationRequest;

@NoArgsConstructor
public class RequestMapper {

    public static ParticipationRequestDto FromRequestTpRequestDto(ParticipationRequest participationrequest) {
        return new ParticipationRequestDto(
                participationrequest.getCreatedDateTime(),
                participationrequest.getEvent().getId(),
                participationrequest.getId(),
                participationrequest.getRequester().getId(),
                participationrequest.getStatus());
    }
}