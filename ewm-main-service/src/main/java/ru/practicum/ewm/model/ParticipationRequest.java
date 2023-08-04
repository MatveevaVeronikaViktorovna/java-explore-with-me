package ru.practicum.ewm.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "participation_requests", uniqueConstraints = { @UniqueConstraint(name = "UniqueEventAndRequester",
        columnNames = { "event", "requester" }) })
public class ParticipationRequest {

    @Column(name = "created")
    @NotNull
    LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @NotNull
    Event event;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    @NotNull
    User requester;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    ParticipationRequestStatus status;

}
