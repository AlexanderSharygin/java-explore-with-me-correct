package ru.practicum.ewm.main_service.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main_service.user.dto.UserDto;
import ru.practicum.ewm.main_service.user.dto.UserShortDto;
import ru.practicum.ewm.main_service.user.model.User;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDtoFromUser(User user) {
        return new UserDto(user.getEmail(), user.getId(), user.getName());
    }

    public static User toUserFromUserDto(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public static UserShortDto fromUserToUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}