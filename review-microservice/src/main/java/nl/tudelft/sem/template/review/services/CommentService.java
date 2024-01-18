package nl.tudelft.sem.template.review.services;

import java.util.List;
import nl.tudelft.sem.template.model.Comment;
import org.springframework.http.ResponseEntity;

public interface CommentService {
    ResponseEntity<Comment> add(Comment comment);

    ResponseEntity<Comment> get(Long commentId);

    ResponseEntity<List<Comment>> getAll(Long reviewId);

    ResponseEntity<Comment> update(Long userId, Comment comment);

    ResponseEntity<String> delete(Long commentId, Long userId);


    List<Long> findMostUpvotedCommentAndReview(Long bookId);

    ResponseEntity<String> addVote(Long commentId, Integer body);
}
