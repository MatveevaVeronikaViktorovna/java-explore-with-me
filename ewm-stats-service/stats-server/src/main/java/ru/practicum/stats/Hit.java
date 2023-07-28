package ru.practicum.stats;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "app")
    String app;
    @Column(name = "uri")
    String uri;
    @Column(name = "ip")
    String ip;
    @Column(name = "created")
    LocalDateTime timestamp;
}
