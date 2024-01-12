package nl.tudelft.sem.template.review.Exceptions;

public class CustomBadRequestException extends RuntimeException{
    public CustomBadRequestException(String message) {
        super(message);
    }

}

