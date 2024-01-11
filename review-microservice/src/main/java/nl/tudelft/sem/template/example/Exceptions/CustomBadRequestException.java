package nl.tudelft.sem.template.example.Exceptions;

public class CustomBadRequestException extends RuntimeException{
    public CustomBadRequestException(String message) {
        super(message);
    }

}

