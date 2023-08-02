package ru.practicum.ewm.dto.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.TYPE_USE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckDateValidator.class)
public @interface StartTwoHoursAfterNowDateValid {
    String message() default "Дата и время, на которые намечено событие не может быть раньше, чем через два часа " +
            "от текущего момента";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
