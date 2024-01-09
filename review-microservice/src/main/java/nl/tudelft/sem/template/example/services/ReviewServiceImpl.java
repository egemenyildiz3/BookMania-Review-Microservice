package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.domain.review.filter.HighestRatedFilter;
import nl.tudelft.sem.template.example.domain.review.filter.MostRecentFilter;
import nl.tudelft.sem.template.example.domain.review.filter.MostRelevantFilter;
import nl.tudelft.sem.template.example.domain.review.filter.ReviewFilter;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewServiceImpl implements ReviewService{
    private final ReviewRepository repo;
    private final CommunicationServiceImpl communicationService;

    @Autowired
    public ReviewServiceImpl(ReviewRepository repo, CommunicationServiceImpl communicationService) {
        this.communicationService = communicationService;
        this.repo = repo;
    }

    private static final List<String> profanities = Arrays.asList("fuck","shit", "motherfucker", "bastard","cunt", "bitch");

    public ReviewFilter getFilter(String type) {
        if (type.equals("mostRecent")) {
            return new MostRecentFilter();
        }
        if (type.equals("highestRated")) {
            return new HighestRatedFilter();
        }
        if (type.equals("mostRelevant")) {
            return new MostRelevantFilter();
        }
        return null;
    }

    @Override
    public ResponseEntity<Review> add(Review review) {
        if(review == null) {
            return ResponseEntity.badRequest().build();
        }
        boolean book = communicationService.existsBook(review.getBookId());
        if(!book){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Invalid Book", "book id not found")
                    .build();
        }
        boolean user = communicationService.existsUser(review.getUserId());
        if(!user){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Invalid User", "user id not found")
                    .build();
        }
        if(checkProfanities(review.getText()))
            return ResponseEntity.status(406).header("Profanities", "Profanities were detected in text").build();

        review.setDownvote(0L);
        review.setUpvote(0L);
        review.setCommentList(new ArrayList<>());
        review.setReportList(new ArrayList<>());
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
    public ResponseEntity get(Long reviewId) {
        if(!repo.existsById(reviewId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Invalid ReviewId", "review Id not found").build();
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

        ReviewFilter reviewFilter = getFilter(filter);

        return ResponseEntity.ok(reviewFilter.filter(listOfReviews));
    }

    @Override
    public ResponseEntity<Review> update(Long userId, Review review) {

       if (review == null || !repo.existsById(review.getId())){
            return ResponseEntity.badRequest().header("Invalid ReviewId", "review Id not found").build();
        }
        //check for user in database
        boolean user = communicationService.existsUser(userId);
        if(!user){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Invalid User", "user id not found ")
                    .build();
        }
        //check for owner or admin
        boolean isAdmin = communicationService.isAdmin(userId);
        if(!isAdmin && review.getUserId()!=userId ){
            return ResponseEntity.status(403)
                    .header("Permission denied", "user is not owner or admin")
                    .build();
        }
        if(checkProfanities(review.getText()))
            return ResponseEntity.status(406).header("Profanities", "Profanities were detected in text").build();


        Review dataReview = repo.getOne(review.getId());
        dataReview.setLastEditTime(LocalDate.now());
        dataReview.setText(review.getText());
        dataReview.setTitle(review.getTitle());
        dataReview.setRating(review.getRating());
        dataReview.setSpoiler(review.getSpoiler());
        dataReview.setBookNotion(review.getBookNotion());
        Review saved = repo.save(dataReview);
        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<String> delete(Long reviewId, Long userId) {
        if(!repo.existsById(reviewId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Invalid ReviewId", "review Id not found").build();
        }
        Review review = repo.findById(reviewId).get();
        boolean isAdmin = communicationService.isAdmin(userId);//call method for admin check from users

        //check for owner or admin
        if(userId == review.getUserId() || isAdmin){
            repo.deleteById(reviewId);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(403)
                .header("Permission denied", "user is not owner or admin")
                .build();

    }
    @Override
    public ResponseEntity<String> addSpoiler(Long reviewId) {
        if(!repo.existsById(reviewId) || get(reviewId).getBody() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Invalid ReviewId", "review Id not found").build();
        }
        Review review = (Review) get(reviewId).getBody();
        review.spoiler(true);
        repo.save(review);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> addVote(Long reviewId, Integer body) {
        if(!repo.existsById(reviewId) || get(reviewId).getBody() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Invalid ReviewId", "review Id not found").build();
        }
        Review review = (Review) get(reviewId).getBody();
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
