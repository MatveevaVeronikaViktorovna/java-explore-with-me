package ru.practicum.stats;

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
@Table(name = "hits")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "app")
    @NotBlank
    String app;
    @Column(name = "uri")
    @NotBlank
    String uri;
    @Column(name = "ip")
    @NotBlank
    String ip;
    @Column(name = "created")
    @NotNull
    LocalDateTime timestamp;
}
