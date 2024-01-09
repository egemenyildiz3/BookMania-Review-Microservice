package nl.tudelft.sem.template.example.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<String> handleCustomBadRequest(CustomBadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
