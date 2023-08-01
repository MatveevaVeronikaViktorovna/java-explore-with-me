package ru.practicum.ewm.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class CategoryDto {
    Long id;
    @NotBlank(message = "must not be blank")
    @Size(min = 1, max = 50)
    String name;
}
