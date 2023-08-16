package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByIdIn(List<Long> ids);

    @Query("SELECT u FROM User u " +
            "WHERE u.id IN " +
            "(SELECT fr.friend.id FROM FriendRequest fr " +
            "WHERE fr.requester.id = :userId AND fr.status = 'CONFIRMED') " +
            "OR u.id IN " +
            "(SELECT fr.requester.id FROM FriendRequest fr " +
            "WHERE fr.friend.id = :userId AND fr.status = 'CONFIRMED')")
    List<User> findUserFriends(@Param("userId") Long userId);

}
