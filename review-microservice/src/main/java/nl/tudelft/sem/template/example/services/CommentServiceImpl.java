package nl.tudelft.sem.template.example.services;

import java.util.*;
import java.time.*;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.example.repositories.CommentRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final ReviewRepository reviewRepository;
    @Autowired
    public CommentServiceImpl(CommentRepository repository, ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
        this.repository = repository;
    }

    private static final List<String> profanities = Arrays.asList("fuck", "shit", "motherfucker", "bastard", "cunt", "bitch");

    public static boolean checkProfanities(String text){
        if(text != null){
            for (String character: profanities){
                if(text.toLowerCase().contains(character)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ResponseEntity<Comment> add(Long userId, Long reviewId, Comment comment) {

        if (comment == null || !reviewRepository.existsById(reviewId)) {
            return ResponseEntity.badRequest().build();
        }
        if (checkProfanities(comment.getText())) {
            return ResponseEntity.badRequest().build();
        }
        Review review = reviewRepository.findById(reviewId).get();
        comment.setId(0L);
        comment.setUserId(userId);
        comment.setDownvote(0L);
        comment.setUpvote(0L);
        comment.setTimeCreated(LocalDate.now());
        comment.setReviewId(reviewId);
        comment.setReportList(new ArrayList<>());
        //Comment added = repository.save(comment);
        review.addCommentListItem(comment);
        reviewRepository.save(review);
        review.getCommentList().sort(Comparator.comparing(Comment::getTimeCreated));
        return ResponseEntity.ok(review.getCommentList().get(review.getCommentList().size()-1));
    }

    @Override
    public ResponseEntity<Comment> get(Long commentId) {
        if (!repository.existsById(commentId)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repository.findById(commentId).get());
    }

    @Override
    public ResponseEntity<List<Comment>> getAll(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            return  ResponseEntity.badRequest().build();
        }

        List<Comment> comments = repository.findAll().stream()
                .filter(c -> c.getReviewId().equals(reviewId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);

    }

    @Override
    public ResponseEntity<Comment> update(Long userId, Comment comment) {
        if (comment == null || !Objects.equals(comment.getUserId(), userId) || !repository.existsById(comment.getId())) {
            return ResponseEntity.badRequest().header("blah").build();
        }

        if (checkProfanities(comment.getText())) {
            return ResponseEntity.badRequest().build();
        }
        Comment dataCom = repository.getOne(comment.getId());
        dataCom.setText(comment.getText());
        Comment updated = repository.save(dataCom);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<String> delete(Long commentId, Long userId) {
        if (!repository.existsById(commentId)) {
            return ResponseEntity.badRequest().build();
        }
        Comment comment = repository.findById(commentId).get();
        Review rev = reviewRepository.getOne(comment.getReviewId());
        if (Objects.equals(userId, comment.getUserId())) {
            //repository.deleteById(commentId);
            rev.getCommentList().remove(comment);
            reviewRepository.save(rev);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
