package ru.practicum.ewm.dto.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class NewCompilationDto {
    Set<Long> events;
    Boolean pinned;

    @NotBlank(message = "must not be blank", groups = Create.class)
    @Size(min = 1, max = 50, groups = {Create.class, Update.class})
    String title;
}
