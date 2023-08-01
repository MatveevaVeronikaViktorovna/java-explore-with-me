package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.UserDtoMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.pagination.CustomPageRequest;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper mapper = Mappers.getMapper(UserDtoMapper.class);

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = mapper.dtoToUser(userDto);
        User newUser = userRepository.save(user);
        log.info("Добавлен пользователь: {}", newUser);
        return mapper.userToDto(newUser);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Пользователь с id {} не найден", id);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", id));
        }
        userRepository.deleteById(id);
        log.info("Удален пользователь с id {}", id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        // сначала проверяем ids если не null то делаем запрос к БД findAllByIds
        Pageable page = CustomPageRequest.of(from, size);
        List<User> users;
        if (ids != null) {
            users = userRepository.findAllByIdIn(ids);
        } else {
            users = userRepository.findAll(page).getContent();
        }
        return users
                .stream()
                .map(mapper::userToDto)
                .collect(Collectors.toList());
    }

}
