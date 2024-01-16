package nl.tudelft.sem.template.review.restcontrollers;

import java.util.List;
import nl.tudelft.sem.template.api.CommentApi;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.CommentServiceImpl;
import nl.tudelft.sem.template.review.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.review.services.ReviewServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController implements CommentApi {
    private final CommentServiceImpl service;

    public CommentController(CommentRepository commentRepository, ReviewRepository reviewRepository, CommentServiceImpl service) {
        CommunicationServiceImpl communicationService = new CommunicationServiceImpl();
        this.service = service != null ? service : new CommentServiceImpl(commentRepository, reviewRepository, communicationService);
    }

    @Override
    public ResponseEntity<String> commentDeleteCommentIdUserIdDelete(Long commentId, Long userId) {
        return service.delete(commentId, userId);
    }

    @Override
    public ResponseEntity<Comment> commentPost(Comment comment) {
        return service.add(comment);
    }

    @Override
    public ResponseEntity<List<Comment>> commentSeeAllReviewIdGet(Long reviewId) {
        return service.getAll(reviewId);
    }

    @Override
    public ResponseEntity<Comment> commentUpdateUserIdPut(Long userId, Comment comment) {
        return service.update(userId, comment);
    }

    @Override
    public ResponseEntity<Comment> commentCommentIdGet(Long commentId) {
        return service.get(commentId);
    }

    @Override
    public ResponseEntity<String> commentVoteCommentIdVotePut(Long commentId, Integer body) {
        return service.addVote(commentId, body);
    }
}
