package nl.tudelft.sem.template.review.services;

import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.BookData;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class StatsServiceImpl implements  StatsService {
    private final BookDataRepository bookDataRepository;
    private final ReviewRepository reviewRepository;

    public StatsServiceImpl(BookDataRepository bookDataRepository, ReviewRepository reviewRepository) {
        this.bookDataRepository = bookDataRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ResponseEntity<Double> avgRating (Long bookId) {
        if(!bookDataRepository.existsById(bookId)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<BookData> optionalBookData = bookDataRepository.findById(bookId);
        if (optionalBookData.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BookData bookData = optionalBookData.get();

        return ResponseEntity.ok(bookData.getAvrRating());
    }

    @Override
    public ResponseEntity<Long> interactions(Long bookId) {
        if(!bookDataRepository.existsById(bookId)) {
            return ResponseEntity.badRequest().build();
        }

        Long count = reviewRepository.countByBookId(bookId);
        return ResponseEntity.ok(count);
    }

}
