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
import org.springframework.http.HttpStatus;
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

        comment.setUserId(userId);
        comment.setTimeCreated(LocalDate.now());
        comment.setReview(review);
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
                .filter(c -> c.getReview().getId().equals(reviewId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);

    }

    @Override
    public ResponseEntity<Comment> update(Long userId, Comment comment) {
        if (comment == null || !Objects.equals(comment.getUserId(), userId) || !repository.existsById(comment.getId())) {
            return ResponseEntity.badRequest().build();
        }
        System.out.println(comment.getId());
        System.out.println(comment.getReview());
        comment.setReview(repository.findById(comment.getId()).get().getReview());
        if (checkProfanities(comment.getText())) {
            return ResponseEntity.badRequest().build();
        }
        Comment updated = repository.save(comment);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<String> delete(Long commentId, Long userId) {
        if (!repository.existsById(commentId)) {
            return ResponseEntity.badRequest().build();
        }
        Comment comment = repository.findById(commentId).get();
        Review rev = comment.getReview();
        if (Objects.equals(userId, comment.getUserId())) {
            //repository.deleteById(commentId);
            rev.getCommentList().remove(comment);
            reviewRepository.save(rev);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<String> addVote(Long commentId, Integer body) {
        if(!repository.existsById(commentId) || get(commentId).getBody() == null || !(List.of(0, 1).contains(body))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Invalid commentId", "comment Id not found").build();
        }
        Comment comment = get(commentId).getBody();
        if(body == 1) {
            if (comment.getUpvote() == null) {
                comment.setUpvote(0L);
            }
            comment.upvote(comment.getUpvote() + 1);
        } else {
            if (comment.getDownvote() == null) {
                comment.setDownvote(0L);
            }
            comment.downvote(comment.getDownvote() + 1);
        }
        repository.save(comment);
        return ResponseEntity.ok().build();
    }


}
