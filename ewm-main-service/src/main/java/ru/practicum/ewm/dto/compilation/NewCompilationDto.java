package ru.practicum.ewm.dto.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class NewCompilationDto {
    Set<Long> events;
    Boolean pinned;

    @NotBlank(message = "must not be blank")
    @Size(min = 1, max = 50)
    String title;
}
