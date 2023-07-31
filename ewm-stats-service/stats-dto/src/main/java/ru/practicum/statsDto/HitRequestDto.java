package ru.practicum.statsDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

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
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime timestamp;
}
