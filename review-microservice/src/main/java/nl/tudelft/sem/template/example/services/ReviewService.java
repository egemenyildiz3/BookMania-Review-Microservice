package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    public ResponseEntity<Review> add(Review review);
    public ResponseEntity<Review> get(Long reviewId);
    public ResponseEntity<Review> update(Long userId, Review review);
    public ResponseEntity<String> delete(Long reviewId, Long userId);


}
