package ru.practicum.ewm.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = {@JoinColumn(name = "compilation_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id")})
    Set<Event> events = new HashSet<>();

    @Column(name = "pinned")
    Boolean pinned;

    @Column(name = "title")
    @NotBlank
    String title;

    @PrePersist
    public void prePersist() {
        if (pinned == null) {
            pinned = false;
        }
    }

}
