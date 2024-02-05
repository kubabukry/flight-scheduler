package pl.edu.wieik.flightScheduler.controller;

import pl.edu.wieik.flightScheduler.exception.AlreadyExistsException;
import pl.edu.wieik.flightScheduler.exception.ErrorResponse;
import pl.edu.wieik.flightScheduler.exception.NoSuchContent;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
@ResponseBody
public class ExceptionController {
    @ExceptionHandler(value = AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExists(AlreadyExistsException e){
        return new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
    }

    @ExceptionHandler(value = NoSuchContent.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchContent(NoSuchContent e){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException e) {

        List<String> messages = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage).toList();
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), messages.toString());
    }
}
