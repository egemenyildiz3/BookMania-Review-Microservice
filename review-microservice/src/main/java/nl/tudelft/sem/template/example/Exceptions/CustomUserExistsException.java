package nl.tudelft.sem.template.example.Exceptions;

public class CustomUserExistsException extends RuntimeException {
    public CustomUserExistsException(String message) {
        super(message);
    }
}

