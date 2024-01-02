package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<Review>> seeAll(Long bookId, String filter) {
        List<Review> listOfReviews = repo.findAll().stream()
                .filter(r -> r.getBookId().equals(bookId))
                .collect(Collectors.toList());

        if (listOfReviews.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!Arrays.asList("mostRelevant", "mostRecent", "highestRated").contains(filter)) {
            return ResponseEntity.badRequest().build();
        }

        switch (filter) {
            case "mostRecent" -> listOfReviews.sort(Comparator.comparing(Review::getTimeCreated).reversed());
            case "highestRated" -> listOfReviews.sort(Comparator.comparing(Review::getUpvote).reversed());
            case "mostRelevant" -> listOfReviews.sort(Comparator.comparingLong(
                    review -> review.getDownvote() - review.getUpvote()
            ));
        }

        return ResponseEntity.ok(listOfReviews);
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
    public ResponseEntity<String> delete(Long reviewId, Long userId) {
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
        //TODO: call the method from user microservice that returns the role of user
        // return true if admin
        return true;
    }

    @Override
    public ResponseEntity<String> addSpoiler(Long reviewId) {
        if(!repo.existsById(reviewId) || get(reviewId).getBody() == null) {
            return ResponseEntity.badRequest().build();
        }
        Review review = get(reviewId).getBody();
        review.spoiler(true);
        repo.save(review);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> addVote(Long reviewId, Integer body) {
        if(!repo.existsById(reviewId) || get(reviewId).getBody() == null) {
            return ResponseEntity.badRequest().build();
        }
        Review review = get(reviewId).getBody();
        if (body == 1) {
            if (review.getUpvote() == null) {
                review.setUpvote(0L);
            }
            review.upvote(review.getUpvote() + 1);
        } else {
            if (review.getDownvote() == null) {
                review.setDownvote(0L);
            }
            review.downvote(review.getDownvote() + 1);
        }
        repo.save(review);
        return ResponseEntity.ok().build();
    }
}
