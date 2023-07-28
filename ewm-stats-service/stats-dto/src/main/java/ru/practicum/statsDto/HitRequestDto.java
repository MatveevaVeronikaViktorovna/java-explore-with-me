package ru.practicum.statsDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class HitRequestDto {
    @NotBlank
    String app;
    @NotBlank
    String uri;
    @NotBlank
    String ip;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
