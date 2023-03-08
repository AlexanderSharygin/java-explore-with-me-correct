package ru.practicum.ewm.main_service.participate_request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.participate_request.model.ParticipationRequest;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(ParticipationRequest participationrequestd) {
        if (participationrequestd == null) {
            return null;
        }

        return new ParticipationRequestDto(
                participationrequestd.getCreatedDateTime(),
                participationrequestd.getEvent().getId(),
                participationrequestd.getId(),
                participationrequestd.getRequester().getId(),
                participationrequestd.getStatus());
    }
}
