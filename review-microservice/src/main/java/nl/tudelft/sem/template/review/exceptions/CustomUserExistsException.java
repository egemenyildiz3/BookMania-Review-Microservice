package nl.tudelft.sem.template.review.exceptions;

public class CustomUserExistsException extends RuntimeException {
    public CustomUserExistsException(String message) {
        super(message);
    }
}

