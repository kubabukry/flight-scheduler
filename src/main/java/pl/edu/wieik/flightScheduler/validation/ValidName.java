package pl.edu.wieik.flightScheduler.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@NotBlank(message = "first name is mandatory")
@Size(min = 1, max = 25, message = "name must be between 1 and 25 characters long")
@Pattern(regexp = "[a-zA-Z]+", message = "name can contain only letters")
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Constraint(validatedBy = { })
public @interface ValidName {
    String message() default "{jakarta.validation.constraints.Pattern.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
