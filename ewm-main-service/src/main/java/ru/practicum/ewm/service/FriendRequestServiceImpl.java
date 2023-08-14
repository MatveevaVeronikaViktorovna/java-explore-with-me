package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.friendRequest.FriendRequestDto;
import ru.practicum.ewm.dto.friendRequest.UpdateFriendRequestDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IncorrectlyMadeRequestException;
import ru.practicum.ewm.mapper.FriendRequestDtoMapper;
import ru.practicum.ewm.mapper.UserDtoMapper;
import ru.practicum.ewm.model.FriendRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.FriendRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final FriendRequestDtoMapper requestDtoMapper = Mappers.getMapper(FriendRequestDtoMapper.class);
    private final UserDtoMapper userDtoMapper = Mappers.getMapper(UserDtoMapper.class);

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

        Optional<FriendRequest> requestByFriendOptional = requestRepository.findByRequesterIdAndFriendId(friendId,
                requesterId);
        if (requestByFriendOptional.isPresent()) {
            FriendRequest requestByFriend = requestByFriendOptional.get();
            if (requestByFriend.getStatus().equals(RequestStatus.CANCELED)) {
                requestByFriend.setRequester(requester);
                requestByFriend.setFriend(friend);
                requestByFriend.setStatus(RequestStatus.PENDING);
                requestByFriend.setCreated(LocalDateTime.now());
                return requestDtoMapper.friendRequestToDto(requestByFriend);
            } else {
                log.warn("Обработайте входящую заявку в друзья от пользователя с id {} к пользователю с id {}",
                        friendId, requesterId);
                throw new IncorrectlyMadeRequestException(String.format("Process incoming friend request from user" +
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

    @Transactional(readOnly = true)
    @Override
    public List<FriendRequestDto> getAllOutgoingFriendRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        List<FriendRequest> requests = requestRepository.findAllByRequesterIdAndStatusNot(userId, RequestStatus.CONFIRMED);
        return requests
                .stream()
                .map(requestDtoMapper::friendRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<FriendRequestDto> getAllIncomingFriendRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        List<FriendRequest> requests = requestRepository.findAllByFriendIdAndStatusIn(userId,
                List.of(RequestStatus.PENDING, RequestStatus.REJECTED));
        return requests
                .stream()
                .map(requestDtoMapper::friendRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FriendRequestDto> updateIncomingFriendRequestsStatus(Long userId, UpdateFriendRequestDto requestDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        List<FriendRequest> requests = requestRepository.findAllByFriendIdAndIdIn(userId, requestDto.getRequestIds());

        if (requestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (FriendRequest request : requests) {
                if (!(request.getStatus().equals(RequestStatus.PENDING) || request.getStatus().equals(RequestStatus.REJECTED))) {
                    log.warn("Невозможно подтвердить заявку, которая находится в неподходящем статусе: {}", request.getStatus());
                    throw new ConditionsNotMetException("Request cannot be confirmed because it's not in the right " +
                            "status: " + request.getStatus());
                }
                request.setStatus(RequestStatus.CONFIRMED);
            }
        } else if (requestDto.getStatus().equals(RequestStatus.REJECTED)) {
            for (FriendRequest request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    log.warn("Невозможно отклонить заявку, которая находится в неподходящем статусе: {}", request.getStatus());
                    throw new ConditionsNotMetException("Request cannot be rejected because it's not in the right " +
                            "status: " + request.getStatus());
                }
                request.setStatus(RequestStatus.REJECTED);
            }
        } else {
            log.warn("Новый статус для заявок в друзья должен быть CONFIRMED or REJECTED");
            throw new ConditionsNotMetException("New status of friend requests must be CONFIRMED or REJECTED");
        }

        log.info("Пользователем с id {} обновлен статус входящих заявок в друзья: {}", userId, requests);
        return requests
                .stream()
                .map(requestDtoMapper::friendRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FriendRequestDto> updateOutgoingFriendRequestsStatus(Long userId, UpdateFriendRequestDto requestDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        List<FriendRequest> requests = requestRepository.findAllByRequesterIdAndIdIn(userId, requestDto.getRequestIds());

        if (requestDto.getStatus().equals(RequestStatus.PENDING)) {
            for (FriendRequest request : requests) {
                if (!request.getStatus().equals(RequestStatus.CANCELED)) {
                    log.warn("Невозможно перевести в рассмотрение заявку, которая находится в неподходящем статусе: {}", request.getStatus());
                    throw new ConditionsNotMetException("Request cannot be pending because it's not in the right " +
                            "status: " + request.getStatus());
                }
                request.setStatus(RequestStatus.PENDING);
            }
        } else if (requestDto.getStatus().equals(RequestStatus.CANCELED)) {
            for (FriendRequest request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    log.warn("Невозможно отменить заявку, которая находится в неподходящем статусе: {}", request.getStatus());
                    throw new ConditionsNotMetException("Request cannot be cancelled because it's not in the right " +
                            "status: " + request.getStatus());
                }
                request.setStatus(RequestStatus.CANCELED);
            }
        } else {
            log.warn("Новый статус для заявок в друзья должен быть PENDING or CANCELED");
            throw new ConditionsNotMetException("New status of friend requests must be CONFIRMED or REJECTED");
        }

        log.info("Пользователем с id {} обновлен статус исходящих заявок в друзья: {}", userId, requests);
        return requests
                .stream()
                .map(requestDtoMapper::friendRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<FriendRequestDto> updateFriendshipStatus(Long userId, UpdateFriendRequestDto requestDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        List<FriendRequest> requests = requestRepository.findAllFriendsByIdIn(userId, requestDto.getRequestIds());

        if (requestDto.getStatus().equals(RequestStatus.REJECTED)) {
            if (requests.isEmpty()) {
                log.warn("Заявки с id {} не являются подтвержденными заявками в друзья", requestDto.getRequestIds());
                throw new ConditionsNotMetException("These requests are not confirmed friend requests"
                        + requestDto.getRequestIds());
            }
            for (FriendRequest request : requests) {
                User requester = request.getRequester();
                User friend = request.getFriend();
                if (userId.equals(requester.getId())) {
                    request.setRequester(friend);
                    request.setFriend(requester);
                }
                request.setStatus(RequestStatus.REJECTED);
            }
        } else {
            log.warn("Новый статус для подтвержденных заявок в друзья должен быть REJECTED");
            throw new ConditionsNotMetException("New status of friend requests must be REJECTED");
        }

        log.info("Пользователем с id {} обновлен статус подтвержденных заявок в друзья: {}", userId, requests);
        return requests
                .stream()
                .map(requestDtoMapper::friendRequestToDto)
                .collect(Collectors.toList());
    }

}
