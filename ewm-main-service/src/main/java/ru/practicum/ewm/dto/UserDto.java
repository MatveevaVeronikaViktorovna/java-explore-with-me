package ru.practicum.ewm.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserDto {
    Long id;

    @NotBlank(message = "must not be blank")
    @Size(min = 2, max = 250)
    String name;

    @NotBlank(message = "must not be blank")
    @Size(min = 6, max = 254)
    @Email
    String email;
}
