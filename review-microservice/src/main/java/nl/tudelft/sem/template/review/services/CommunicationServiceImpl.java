package nl.tudelft.sem.template.review.services;

import org.springframework.stereotype.Service;

@Service
public class CommunicationServiceImpl {
    private final String userMicroUrl = "http://localhost:8080";

    private final String bookMicroUrl = "http://localhost:8081";

    public CommunicationServiceImpl() {
    }


    public boolean isAdmin(Long userId) {
        //TODO make http request to endpoint for admin
        return true;
    }

    public boolean existsBook(Long bookId) {
        //TODO make http request to endpoint for book
        return true;
    }

    public boolean existsUser(Long userId) {
        //TODO make http request to endpoint for user
        return true;
    }


}
