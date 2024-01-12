package nl.tudelft.sem.template.example.services;

import java.util.*;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    ResponseEntity<Comment> add(Comment comment);
    ResponseEntity<Comment> get(Long commentId);
    ResponseEntity<List<Comment>> getAll(Long reviewId);
    ResponseEntity<Comment> update(Long userId, Comment comment);
    ResponseEntity<String> delete(Long commentId, Long userId);

    public ResponseEntity<Long> findMostUpvotedComment(Long bookId);
}
