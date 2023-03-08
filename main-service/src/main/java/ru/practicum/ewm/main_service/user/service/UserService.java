package ru.practicum.ewm.main_service.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main_service.exception.model.ConflictException;
import ru.practicum.ewm.main_service.exception.model.NotFoundException;
import ru.practicum.ewm.main_service.user.dto.UserDto;
import ru.practicum.ewm.main_service.user.mapper.UserMapper;
import ru.practicum.ewm.main_service.user.model.User;
import ru.practicum.ewm.main_service.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll(List<Long> usersId, Pageable pageable) {
        if (usersId == null) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDtoFromUser)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(usersId, pageable).stream()
                    .map(UserMapper::toUserDtoFromUser)
                    .collect(Collectors.toList());
        }
    }

    public UserDto getById(long userId) {
        User user = getUserIfExist(userId);
        return UserMapper.toUserDtoFromUser(user);
    }

    public UserDto create(UserDto userDto) {
        Optional<User> UserWithSameName = userRepository.findByName(userDto.getName());
        if (UserWithSameName.isPresent()) {
            throw new ConflictException("User with name " + userDto.getName() + " already exists in the DB");
        }

        return UserMapper.toUserDtoFromUser(userRepository.save(UserMapper.toUserFromUserDto(userDto)));
    }

    public void delete(long userId) {
        User user = getUserIfExist(userId);
        userRepository.delete(user);
    }

    private User getUserIfExist(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id" + userId + " not exists in the DB."));
    }
}
