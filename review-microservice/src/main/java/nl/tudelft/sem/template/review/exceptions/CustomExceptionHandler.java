package nl.tudelft.sem.template.review.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<String> handleCustomBadRequest(CustomBadRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(CustomProfanitiesException.class)
    public ResponseEntity<String> handleProfanitiesRequest(CustomProfanitiesException e) {
        return ResponseEntity.status(406).body(e.getMessage());
    }

    @ExceptionHandler(CustomPermissionsException.class)
    public ResponseEntity<String> handleNoPermissionRequest(CustomPermissionsException e) {
        return ResponseEntity.status(403).body(e.getMessage());
    }

    @ExceptionHandler(CustomUserExistsException.class)
    public ResponseEntity<String> handleUserDoesntExistRequest(CustomUserExistsException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }
}
