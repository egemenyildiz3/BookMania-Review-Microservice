package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;

public interface GetReportService {
    ResponseEntity<BookData> getReport(Long bookId, String userId, String info);
}
