package nl.tudelft.sem.template.example.RESTcontrollers;

import nl.tudelft.sem.template.api.CommentApi;
import nl.tudelft.sem.template.example.repositories.CommentRepository;
import nl.tudelft.sem.template.example.services.CommentServiceImpl;
import nl.tudelft.sem.template.model.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController implements CommentApi {
    private final CommentServiceImpl service;
    public CommentController(CommentRepository repository) {
        this.service = new CommentServiceImpl(repository);
    }

    @Override
    public ResponseEntity<String> commentDeleteCommentIdUserIdDelete(Long commentId, Long userId) {
        return service.delete(commentId, userId);
    }

    @Override
    public ResponseEntity<Comment> commentAddReviewIdUserIdPost(Long userId, Long reviewId, Comment comment) {
        return service.add(userId, reviewId, comment);
    }

    @Override
    public ResponseEntity<Comment> commentCommentIdGet(Long commentId) {
        return service.get(commentId);
    }

    @Override
    public ResponseEntity<Comment> commentEditUserIdPut(Long userId, Comment comment) {
        return service.update(userId, comment);
    }
}
