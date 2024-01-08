package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.ReviewApi;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.example.services.ReviewServiceImpl;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewController implements ReviewApi {

    private final ReviewServiceImpl service;

    public ReviewController(ReviewRepository repo) {
        CommunicationServiceImpl communicationService = new CommunicationServiceImpl();
        this.service = new ReviewServiceImpl(repo,communicationService);
    }


    @Override
    public ResponseEntity<String> reviewDeleteReviewIdUserIdDelete(Long reviewId, Long userId) {
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
    public ResponseEntity<List<Review>> reviewSeeAllBookIdFilterGet(Long bookId, String filter) {
        return service.seeAll(bookId, filter);
    }

    @Override
    public ResponseEntity<Review> reviewUpdateUserIdPut(Long userId, Review review) {
        return service.update(userId,review);
    }

    @Override
    public ResponseEntity<String> reviewSpoilerReviewIdPut(Long reviewId) {
        return service.addSpoiler(reviewId);
    }

    @Override
    public ResponseEntity<String> reviewVoteReviewIdPut(Long reviewId, Integer body) {
        return service.addVote(reviewId, body);
    }

    @Override
    public ResponseEntity<List<Review>> reviewMostUpvotedUserIdGet(Long userId) {
        return service.mostUpvotedReviews(userId);
    }

    @Override
    public ResponseEntity<String> reviewPinReviewIdPut(Long reviewId, Boolean body) {
        return service.pinReview(reviewId, body);
    }
}
