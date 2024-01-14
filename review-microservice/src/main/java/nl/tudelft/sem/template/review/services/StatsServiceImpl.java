package nl.tudelft.sem.template.review.services;

import java.util.Optional;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
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
        Optional<BookData> optionalBookData = bookDataRepository.findById(bookId);
        if (optionalBookData.isEmpty()) {
            throw new CustomBadRequestException("Book not found.");
        }

        BookData bookData = optionalBookData.get();

        return ResponseEntity.ok(bookData.getAvrRating());
    }

    @Override
    public ResponseEntity<Long> interactions(Long bookId) {
        if (!bookDataRepository.existsById(bookId)) {
            throw new CustomBadRequestException("Book data not found.");
        }

        Long count = reviewRepository.countByBookId(bookId);
        return ResponseEntity.ok(count);
    }

}
