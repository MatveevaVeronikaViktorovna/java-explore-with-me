package ru.practicum.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.statsDto.HitResponseDto(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.uri, h.app  " +
            "ORDER BY count(h.ip) desc")
    List<HitResponseDto> findDistinctByTimestampAndUris(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end,
                                                        @Param("uris") List<String> uris);


    @Query("SELECT new ru.practicum.statsDto.HitResponseDto(h.app, h.uri, count(h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.uri, h.app  " +
            "ORDER BY count(h.ip) desc")
    List<HitResponseDto> findAllByTimestampAndUris(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end,
                                                   @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.statsDto.HitResponseDto(h.app, h.uri, count(DISTINCT h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.uri, h.app  " +
            "ORDER BY count(h.ip) desc")
    List<HitResponseDto> findDistinctByTimestamp(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);


    @Query("SELECT new ru.practicum.statsDto.HitResponseDto(h.app, h.uri, count(h.ip)) " +
            "FROM Hit as h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.uri, h.app " +
            "ORDER BY count(h.ip) desc")
    List<HitResponseDto> findAllByTimestamp(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

}
