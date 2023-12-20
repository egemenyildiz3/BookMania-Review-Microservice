package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.model.Review;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository repo;
    public ReviewServiceImpl(ReviewRepository repo) {
        this.repo = repo;
    }

    private static final List<String> profanities = Arrays.asList("fuck","shit", "motherfucker", "bastard","cunt", "bitch");

    @Override
    public ResponseEntity<Review> add(Review review) {
        if(review == null) {
            return ResponseEntity.badRequest().build();
        }
        if(checkProfanities(review.getText()))
            return ResponseEntity.badRequest().build();

        review.lastEditTime(LocalDate.now());
        review.timeCreated(LocalDate.now());
        Review saved = repo.save(review);
        return ResponseEntity.ok(saved);
    }
    public static boolean checkProfanities(String s){
        if(s!=null){
            for (String p: profanities){
                if(s.toLowerCase().contains(p)){
                   return true;
                }
            }
        }
        return false;
    }

    @Override
    public ResponseEntity<Review> get(Long reviewId) {
        if(!repo.existsById(reviewId)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(reviewId).get());
    }

    @Override
    public ResponseEntity<Review> update(Long userId, Review review) {
        if (review == null || review.getUserId()!=userId || !repo.existsById(review.getId())){
            return ResponseEntity.badRequest().build();
        }
        if(checkProfanities(review.getText()))
            return ResponseEntity.badRequest().build();
        review.lastEditTime(LocalDate.now());
        Review saved = repo.save(review);
        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<Void> delete(Long reviewId, Long userId) {
        if(!repo.existsById(reviewId)) {
            return ResponseEntity.badRequest().build();
        }
        Review review = repo.findById(reviewId).get();
        boolean isAdmin = isAdmin(userId);//call method for admin check from users
        if(userId == review.getUserId() || isAdmin){
            repo.deleteById(reviewId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();

    }
    public static boolean isAdmin(Long userId){
        //get the user role from user microservice
        // if(getRole(userId) == admin)
        // return true;
        //else return false;
        return true;
    }
}
