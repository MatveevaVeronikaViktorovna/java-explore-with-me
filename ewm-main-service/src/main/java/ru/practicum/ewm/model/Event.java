package ru.practicum.ewm.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "annotation")
    @NotBlank
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull
    Category category;

    @Column(name = "description")
    @NotBlank
    String description;

    @Column(name = "event_date")
    @NotNull
    LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    @NotNull
    Location location;

    @Column(name = "paid")
    @NotNull
    Boolean paid;
    //default: false

    @Column(name = "participant_limit")
    @NotNull
    Integer participantLimit;
    //default: 0

    @Column(name = "requestModeration")
    @NotNull
    Boolean requestModeration;
    //default: true

    @Column(name = "title")
    @NotBlank
    String title;


    @Column(name = "created_on")
    @NotNull
    LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id")
    @NotNull
    User initiator;

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    @NotNull
    EventState state;

 //   @Column(name = "published_on")
 //   LocalDateTime publishedOn;



}
