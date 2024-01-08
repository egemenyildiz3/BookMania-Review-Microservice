package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;

public interface GetReportService {
    ResponseEntity<BookData> getReport(Long bookId, Long userId, String info);

    ResponseEntity<BookData> createBookDataInRepository(Long bookId);

    ResponseEntity<BookData> addRatingAndNotion(Long bookId, Long rating, Review.BookNotionEnum notion);
    ResponseEntity<BookData> removeRatingAndNotion(Long bookId, Long rating, Review.BookNotionEnum notion);
    ResponseEntity<BookData> updateRatingAndNotion(Long bookId, Long oldRating, Review.BookNotionEnum oldNotion,
                                                   Long newRating, Review.BookNotionEnum newNotion);
}

