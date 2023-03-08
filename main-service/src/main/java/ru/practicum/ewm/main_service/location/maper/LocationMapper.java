package ru.practicum.ewm.main_service.location.maper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.location.dto.LocationDto;
import ru.practicum.ewm.main_service.location.model.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }

        LocationDto locationDto = new LocationDto();

        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());

        return locationDto;
    }

    public static Location toLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }

        Location location = new Location();

        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());

        return location;
    }
}
