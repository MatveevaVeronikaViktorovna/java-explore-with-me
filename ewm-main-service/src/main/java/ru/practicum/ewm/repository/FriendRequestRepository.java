package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.FriendRequest;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findByRequesterIdAndFriendId(Long requestId, Long friendId);

}
