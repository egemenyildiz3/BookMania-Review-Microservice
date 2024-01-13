package nl.tudelft.sem.template.review.services;

import java.util.Optional;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements  StatsService {
    private final BookDataRepository bookDataRepository;
    private final ReviewRepository reviewRepository;

    public StatsServiceImpl(BookDataRepository bookDataRepository, ReviewRepository reviewRepository) {
        this.bookDataRepository = bookDataRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ResponseEntity<Double> avgRating(Long bookId) {
        if (!bookDataRepository.existsById(bookId)) {
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
        if (!bookDataRepository.existsById(bookId)) {
            return ResponseEntity.badRequest().build();
        }

        Long count = reviewRepository.countByBookId(bookId);
        return ResponseEntity.ok(count);
    }

}
