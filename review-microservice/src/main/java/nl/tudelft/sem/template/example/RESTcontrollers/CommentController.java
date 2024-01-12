package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.CommentApi;
import nl.tudelft.sem.template.example.repositories.CommentRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.services.CommentServiceImpl;
import nl.tudelft.sem.template.example.services.ReviewServiceImpl;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController implements CommentApi {
    private final CommentServiceImpl service;

    public CommentController(CommentRepository repository, ReviewRepository reviews) {
        this.service = new CommentServiceImpl(repository, reviews);
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
    public ResponseEntity<String> commentVoteCommentIdPut(Long commentId, Integer body) {
        return service.addVote(commentId, body);
    }
}
