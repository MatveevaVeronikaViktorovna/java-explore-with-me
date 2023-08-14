package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.FriendRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findByRequesterIdAndFriendId(Long requesterId, Long friendId);

    @Query("SELECT u FROM User u " +
            "WHERE u.id IN " +
            "(SELECT fr.friend.id FROM FriendRequest fr " +
            "WHERE fr.requester.id = :userId AND fr.status = 'CONFIRMED') " +
            "OR u.id IN " +
            "(SELECT fr.requester.id FROM FriendRequest fr " +
            "WHERE fr.friend.id = :userId AND fr.status = 'CONFIRMED')")
    List<User> findAllFriends(@Param("userId") Long userId);

    List<FriendRequest> findAllByRequesterIdAndStatusNot(Long userId, RequestStatus status);

    List<FriendRequest> findAllByFriendIdAndStatusIn(Long userId, List<RequestStatus> status);

    List<FriendRequest> findAllByFriendIdAndIdIn(Long userId, List<Long> requestsId);

    List<FriendRequest> findAllByRequesterIdAndIdIn(Long userId, List<Long> requestsId);

    @Query("SELECT fr FROM FriendRequest fr " +
            "WHERE (fr.requester.id = :userId AND fr.status = 'CONFIRMED') OR " +
            "(fr.friend.id = :userId AND fr.status = 'CONFIRMED') " +
            "AND fr.id IN :requestsId")
    List<FriendRequest> findAllFriendsByIdIn(@Param("userId") Long userId,
                                             @Param("requestsId") List<Long> requestsId);

}
