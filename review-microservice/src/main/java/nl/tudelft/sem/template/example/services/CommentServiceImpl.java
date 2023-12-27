package nl.tudelft.sem.template.example.services;

import java.util.*;
import java.time.*;

import nl.tudelft.sem.template.example.repositories.CommentRepository;
import nl.tudelft.sem.template.model.Comment;
import org.springframework.http.ResponseEntity;

public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    public CommentServiceImpl(CommentRepository repository) {
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
        if (comment == null || !repository.existsById(reviewId)) {
            return ResponseEntity.badRequest().build();
        }
        if (checkProfanities(comment.getText())) {
            return ResponseEntity.badRequest().build();
        }
        comment.setUserId(userId);
        comment.timeCreated(LocalDate.now());
        Comment added = repository.save(comment);
        return ResponseEntity.ok(added);
    }

    @Override
    public ResponseEntity<Comment> get(Long commentId) {
        if (!repository.existsById(commentId)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repository.findById(commentId).get());
    }

    @Override
    public ResponseEntity<Comment> update(Long userId, Comment comment) {
        if (comment == null || !Objects.equals(comment.getUserId(), userId) || !repository.existsById(comment.getId())) {
            return ResponseEntity.badRequest().build();
        }
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
        if (userId == comment.getUserId()) {
            repository.deleteById(commentId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
