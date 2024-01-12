package nl.tudelft.sem.template.review.Exceptions;

public class CustomUserExistsException extends RuntimeException {
    public CustomUserExistsException(String message) {
        super(message);
    }
}

