package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.Exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.example.Exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.example.Exceptions.CustomProfanitiesException;
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
import java.util.*;
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
            throw new CustomBadRequestException("Review cannot be null.");
        }
        boolean book = communicationService.existsBook(review.getBookId());
        if(!book)
            throw new CustomBadRequestException("Invalid book id.");


        boolean user = communicationService.existsUser(review.getUserId());
        if(!user)
            throw new CustomBadRequestException("Invalid user id.");


        if(checkProfanities(review.getText()))
            throw new CustomProfanitiesException("Profanities detected in text. Please remove them.");

        review.setId(0L);
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
    public ResponseEntity<Review> get(Long reviewId) {
        if(!repo.existsById(reviewId))
            throw new CustomBadRequestException("Invalid review id.");
        Review review = repo.getOne(reviewId);
        return ResponseEntity.ok(review);
    }

    @Override
    public ResponseEntity<List<Review>> seeAll(Long bookId, String filter) {
        List<Review> listOfReviews = repo.findAll().stream()
                .filter(r -> r.getBookId().equals(bookId))
                .collect(Collectors.toList());

        if (listOfReviews.isEmpty()) {
            throw new CustomBadRequestException("No reviews for this book.");
        }
        if (!Arrays.asList("mostRelevant", "mostRecent", "highestRated").contains(filter)) {
            throw new CustomBadRequestException("Invalid filter.");
        }

        ReviewFilter reviewFilter = getFilter(filter);

        return ResponseEntity.ok(reviewFilter.filter(listOfReviews));
    }

    @Override
    public ResponseEntity<Review> update(Long userId, Review review) {

       if (review == null || !repo.existsById(review.getId()))
           throw new CustomBadRequestException("Invalid review id.");

        //check for user in database
        boolean user = communicationService.existsUser(userId);
        if(!user)
            throw new CustomBadRequestException("Invalid user id.");


        //check for owner or admin
        boolean isAdmin = communicationService.isAdmin(userId);
        if(!isAdmin && review.getUserId()!=userId )
            throw new CustomPermissionsException("User is not owner or admin.");


        if(checkProfanities(review.getText()))
            throw new CustomProfanitiesException("Profanities detected in text. Please remove them.");

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
        if(!repo.existsById(reviewId))
            throw new CustomBadRequestException("Invalid review id.");

        Review review = get(reviewId).getBody();
        boolean isAdmin = communicationService.isAdmin(userId);//call method for admin check from users

        //check for owner or admin
        assert review != null;
        if(Objects.equals(userId, review.getUserId()) || isAdmin){
            repo.deleteById(reviewId);
            return ResponseEntity.ok().build();
        }

        throw new CustomPermissionsException("User is not owner or admin.");


    }
    @Override
    public ResponseEntity<String> addSpoiler(Long reviewId) {
        Review review = get(reviewId).getBody();
        assert review != null;
        review.setSpoiler(true);
        repo.save(review);
        return ResponseEntity.ok("Spoiler added.");
    }

    @Override
    public ResponseEntity<String> addVote(Long reviewId, Integer body) {
        Review review = get(reviewId).getBody();
        assert review != null;
        if (body == 1 ) {
            review.upvote(review.getUpvote() + 1);
        } else {
            review.downvote(review.getDownvote() + 1);
        }
        repo.save(review);
        return ResponseEntity.ok("Vote added.");
    }

    @Override
    public ResponseEntity<List<Review>> mostUpvotedReviews(Long userId) {
        List<Review> listOfReviews = new ArrayList<>(repo.findAll());
        List<Review> result = new ArrayList<>();
        for (Review rev : listOfReviews) {
            if (rev.getUserId().equals(userId)) {
                result.add(rev);
            }
        }
        result.sort(Comparator.comparingLong((Review r) -> r.getUpvote() == null ? 0 : r.getUpvote()).reversed());
        List<Review> finalRes = new ArrayList<>();
        if (result.get(0) != null) {
            finalRes.add(result.get(0));
        }
        if (result.get(1) != null) {
            finalRes.add(result.get(1));
        }
        if (result.get(2) != null) {
            finalRes.add(result.get(2));
        }
        return ResponseEntity.ok(finalRes);
    }

    @Override
    public ResponseEntity<String> pinReview(Long reviewId, Boolean body) {
        if(!repo.existsById(reviewId) || get(reviewId).getBody() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Invalid ReviewId", "review Id not found").build();
        }
        Review review = get(reviewId).getBody();
        review.setPinned(body);
        repo.save(review);
        return ResponseEntity.ok().build();
    }
}
