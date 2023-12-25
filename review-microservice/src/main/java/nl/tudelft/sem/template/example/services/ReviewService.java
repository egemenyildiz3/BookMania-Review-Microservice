package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<Review> add(Review review);
    ResponseEntity<Review> get(Long reviewId);
    ResponseEntity<Review> update(Long userId, Review review);
    ResponseEntity<String> delete(Long reviewId, Long userId);
    ResponseEntity<String> addSpoiler(Long reviewId);
    ResponseEntity<String> addVote(Long reviewId, Integer body);


}
