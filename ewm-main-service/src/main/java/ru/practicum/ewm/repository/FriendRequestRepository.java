package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.FriendRequest;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findByRequesterIdAndFriendId(Long requesterId, Long friendId);

    @Query("SELECT fr FROM FriendRequest fr " +
            "WHERE (fr.requester.id = :userId AND fr.status = 'CONFIRMED') OR " +
            "(fr.friend.id = :userId AND fr.status = 'CONFIRMED') " )
    List<FriendRequest> findAllFriends(@Param("userId") Long userId);

    List<FriendRequest> findAllByRequesterIdAndStatusNot(Long userId, RequestStatus status);

    List<FriendRequest> findAllByFriendIdAndStatusIn(Long userId, List<RequestStatus> status);

    List<FriendRequest> findAllByFriendIdAndStatusInAndIdIn(Long userId, List<RequestStatus> status, List<Long> requestsId);


}
