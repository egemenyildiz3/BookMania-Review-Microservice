package nl.tudelft.sem.template.review.restcontrollers.unittests;

import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.restcontrollers.ReviewController;
import nl.tudelft.sem.template.review.services.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.invoke.SerializedLambda;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class ReviewControllerTest {
    ReviewController controller;
    Review rev;
    Long id = 1L;

    @BeforeEach
    void setUp() {
        BookDataRepository bookDataRepository = mock(BookDataRepository.class);
        ReviewRepository repo = mock(ReviewRepository.class);
        CommentRepository cr = mock(CommentRepository.class);
        ReviewServiceImpl service = mock(ReviewServiceImpl.class);
        controller = new ReviewController(bookDataRepository,repo,cr,service);
        rev = new Review(1L,2L,10L,"wow","review",5L);
        when(service.update(1L,rev)).thenReturn(ResponseEntity.ok(rev));
        when(service.add(rev)).thenReturn(ResponseEntity.ok(rev));
        when(service.delete(1L,id)).thenReturn(ResponseEntity.ok("Deleted"));
        when(service.get(1L)).thenReturn(ResponseEntity.ok(rev));
        List<Review> reviewList = List.of(rev);
        when(service.seeAll(id,"mostRelevant")).thenReturn(ResponseEntity.ok(reviewList));
        when(service.mostUpvotedReviews(1L)).thenReturn(ResponseEntity.ok(reviewList));
        when(service.addVote(1L,1)).thenReturn(ResponseEntity.ok("vote added."));
        when(service.addSpoiler(1L)).thenReturn(ResponseEntity.ok("spoiler added."));
        when(service.pinReview(1L,true)).thenReturn(ResponseEntity.ok("pinned review"));


    }

    @Test
    void reviewDeleteReviewIdUserIdDelete() {
        assertEquals(HttpStatus.OK,controller.reviewDeleteReviewIdUserIdDelete(1L,1L).getStatusCode());
        assertNotNull(controller.reviewDeleteReviewIdUserIdDelete(1L,1L).getBody());
    }

    @Test
    void reviewPost() {
        assertEquals(HttpStatus.OK,controller.reviewPost(rev).getStatusCode());
        assertNotNull(controller.reviewPost(rev).getBody());
    }

    @Test
    void reviewReviewIdGet() {
        var response = controller.reviewReviewIdGet(id);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void reviewSeeAllBookIdFilterGet() {
        var response = controller.reviewSeeAllBookIdFilterGet(id,"mostRelevant");
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void reviewUpdateUserIdPut() {
    }

    @Test
    void reviewSpoilerReviewIdPut() {
    }

    @Test
    void reviewVoteReviewIdVotePut() {
    }

    @Test
    void reviewMostUpvotedUserIdGet() {
    }

    @Test
    void reviewPinReviewIdPinValuePut() {
    }
}