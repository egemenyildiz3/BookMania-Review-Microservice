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

    private final ReviewRepository repo;
    private final ReviewServiceImpl service;

    public ReviewController(ReviewRepository repo) {
        this.repo = repo;
        this.service = new ReviewServiceImpl(repo);
    }


    @Override
    public ResponseEntity<Void> reviewDeleteReviewIdUserIdDelete(Long reviewId, Long userId) {
        return ReviewApi.super.reviewDeleteReviewIdUserIdDelete(reviewId, userId);
    }

    @Override
    public ResponseEntity<Review> reviewPost(@RequestBody Review review) {
        return service.add(review);
    }
}
