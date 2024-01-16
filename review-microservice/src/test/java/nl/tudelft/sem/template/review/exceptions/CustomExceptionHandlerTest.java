package nl.tudelft.sem.template.review.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CustomExceptionHandlerTest {
    @Test
    public void testHandleCustomBadRequest() {
        CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();
        CustomBadRequestException customBadRequestException = new CustomBadRequestException("Bad request");

        ResponseEntity<String> responseEntity = exceptionHandler.handleCustomBadRequest(customBadRequestException);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Bad request", responseEntity.getBody());
    }

    @Test
    public void testHandleProfanitiesRequest() {
        CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();
        CustomProfanitiesException customProfanitiesException = new CustomProfanitiesException("Profanity detected");

        ResponseEntity<String> responseEntity = exceptionHandler.handleProfanitiesRequest(customProfanitiesException);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertEquals("Profanity detected", responseEntity.getBody());
    }

    @Test
    public void testHandleNoPermissionRequest() {
        CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();
        CustomPermissionsException customPermissionsException = new CustomPermissionsException("No permission");

        ResponseEntity<String> responseEntity = exceptionHandler.handleNoPermissionRequest(customPermissionsException);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals("No permission", responseEntity.getBody());
    }


    @Test
    public void testCustomUserExistsException() {
        CustomExceptionHandler exceptionHandler = new CustomExceptionHandler();
        CustomUserExistsException customUserExistsException = new CustomUserExistsException("Not a user");

        ResponseEntity<String> responseEntity = exceptionHandler.handleUserDoesntExistRequest(customUserExistsException);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Not a user", responseEntity.getBody());
    }
}
