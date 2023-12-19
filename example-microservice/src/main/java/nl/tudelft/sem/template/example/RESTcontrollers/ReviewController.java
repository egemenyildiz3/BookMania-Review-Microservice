package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.example.api.ReviewApi;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.services.ReviewServiceImpl;
import nl.tudelft.sem.template.example.model.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController implements ReviewApi {

    private final ReviewServiceImpl service;

    public ReviewController(ReviewRepository repo) {
        this.service = new ReviewServiceImpl(repo);
    }


    @Override
    public ResponseEntity<Void> reviewDeleteReviewIdUserIdDelete(Long reviewId, Long userId) {
        return service.delete(reviewId,userId);
    }

    @Override
    public ResponseEntity<Review> reviewPost(Review review) {
        return service.add(review);
    }

    @Override
    public ResponseEntity<Review> reviewReviewIdGet(Long reviewId) {
        return service.get(reviewId);
    }

    @Override
    public ResponseEntity<Review> reviewUpdateUserIdPut(Long userId, Review review) {
        return service.update(userId,review);
    }
}
