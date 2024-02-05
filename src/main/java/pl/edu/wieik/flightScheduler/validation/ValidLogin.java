package pl.edu.wieik.flightScheduler.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank(message = "login is mandatory")
@Size(min = 3, max = 10, message = "login must be between 3 and 10 characters long")
@Pattern(regexp = "^\\w+$", message = "login can contain only letters, numbers or _ character")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Constraint(validatedBy = { })
public @interface ValidLogin {
    String message() default "{jakarta.validation.constraints.Pattern.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
