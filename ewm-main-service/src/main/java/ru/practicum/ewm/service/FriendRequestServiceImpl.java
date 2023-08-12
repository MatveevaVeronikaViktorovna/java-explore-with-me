package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.FriendRequestDto;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IncorrectlyMadeRequestException;
import ru.practicum.ewm.mapper.FriendRequestDtoMapper;
import ru.practicum.ewm.model.FriendRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.FriendRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final FriendRequestDtoMapper requestDtoMapper = Mappers.getMapper(FriendRequestDtoMapper.class);

    @Transactional
    @Override
    public FriendRequestDto createFriendRequest(Long requesterId, Long friendId) {

        User requester = userRepository.findById(requesterId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", requesterId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", requesterId));
        });

        if (requesterId.equals(friendId)) {
            log.warn("Пользователь не может добавить заявку в друзья самому себе");
            throw new ConditionsNotMetException("The user cannot add a friend request to himself");
        }

        User friend = userRepository.findById(friendId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", friendId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", friendId));
        });

        Optional<FriendRequest> existingRequestOptional = requestRepository.findByRequesterIdAndFriendId(requesterId,
                friendId);
        if(existingRequestOptional.isPresent()) {
            FriendRequest existingRequest = existingRequestOptional.get();
            if (existingRequest.getStatus().equals(RequestStatus.CANCELED)) {
                existingRequest.setStatus(RequestStatus.PENDING);
                existingRequest.setCreated(LocalDateTime.now());
                return requestDtoMapper.friendRequestToDto(existingRequest);
            }
        }

        Optional<FriendRequest> requestByFriendOptional = requestRepository.findByRequesterIdAndFriendId(friendId,
                requesterId);
        if(requestByFriendOptional.isPresent()) {
            FriendRequest requestByFriend = requestByFriendOptional.get();
            if (requestByFriend.getStatus().equals(RequestStatus.CANCELED)) {
                requestByFriend.setRequester(requester);
                requestByFriend.setFriend(friend);
                requestByFriend.setStatus(RequestStatus.PENDING);
                requestByFriend.setCreated(LocalDateTime.now());
                return requestDtoMapper.friendRequestToDto(requestByFriend);
            } else {
                log.warn("Обработайте существующую заявку в друзья от пользователя с id {} к пользователю с id {}",
                        friendId, requesterId);
                throw new IncorrectlyMadeRequestException(String.format("Process an existing friend request from user" +
                        " with id %d to user with id %d", friendId, requesterId));
            }
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequester(requester);
        friendRequest.setFriend(friend);
        friendRequest.setStatus(RequestStatus.PENDING);
        friendRequest.setCreated(LocalDateTime.now());

        FriendRequest newFriendRequest = requestRepository.save(friendRequest);
        log.info("Добавлена заявка в друзья от пользователя с id {} к пользователю с id {}", requesterId, friendId);
        return requestDtoMapper.friendRequestToDto(newFriendRequest);
    }

}
