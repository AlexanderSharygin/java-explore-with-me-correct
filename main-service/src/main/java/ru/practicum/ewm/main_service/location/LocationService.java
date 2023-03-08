package ru.practicum.ewm.main_service.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main_service.event_category.dto.CategoryDto;
import ru.practicum.ewm.main_service.event_category.mapper.CategoryMapper;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;
import ru.practicum.ewm.main_service.location.model.Location;

import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location save(Location location) {
        Optional<Location> existedLocation = locationRepository
                .findByLatAndLon(location.getLat(), location.getLon());
        if(existedLocation.isEmpty())
        {
            locationRepository.save(location);
        }
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon()).orElse(null);
    }
}
