package nl.tudelft.sem.template.review.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.domain.reviewsort.*;
import nl.tudelft.sem.template.review.domain.textcheck.ProfanityHandler;
import nl.tudelft.sem.template.review.domain.textcheck.TextHandler;
import nl.tudelft.sem.template.review.domain.textcheck.UrlHandler;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomUserExistsException;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository repo;
    private final CommunicationServiceImpl communicationService;

    private final GetReportServiceImpl getReportService;

    /**
     * Initializes a new instance of the class with the provided dependencies.
     *
     * @param getReportService       The GetReportService instance for retrieving reports.
     * @param repo                   The repo instance for data storage and retrieval.
     * @param communicationService   The CommunicationService instance for using other teams apis.
     */
    public ReviewServiceImpl(GetReportServiceImpl getReportService,
                             ReviewRepository repo,
                             CommunicationServiceImpl communicationService) {
        this.communicationService = communicationService;
        this.getReportService = getReportService;
        this.repo = repo;
    }


    @Override
    public ResponseEntity<Review> add(Review review) {
        if (review == null) {
            throw new CustomBadRequestException("Review cannot be null.");
        }

        boolean existsBook = communicationService.existsBook(review.getBookId());
        if (!existsBook) {
            throw new CustomBadRequestException("Invalid book id.");
        }


        boolean existsUser = communicationService.existsUser(review.getUserId());
        if (!existsUser) {
            throw new CustomUserExistsException("Invalid user id.");
        }

        if (repo.existsByBookIdAndUserId(review.getBookId(), review.getUserId())) {
            throw new CustomBadRequestException("User has already created review for book");
        }


        handleText(review.getText());
        handleText(review.getTitle());
        updateBookData(review);

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

    public void updateBookData(Review review) {
        getReportService.addRatingAndNotion(review.getBookId(), review.getRating(), review.getBookNotion());
    }

    @Override
    public ResponseEntity<Review> get(Long reviewId) {
        if (!repo.existsById(reviewId)) {
            throw new CustomBadRequestException("Invalid review id.");
        }
        Review review = new Review(1L, 2L, 10L, "Review", "review", 5L);
        if (repo.findById(reviewId).isPresent()) {
            review = repo.findById(reviewId).get();
        }
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

        SortContext sortContext = new SortContext();

        switch (filter) {
            case "mostRelevant" -> sortContext.setSort(new MostRelevantSort());
            case "mostRecent" -> sortContext.setSort(new MostRecentSort());
            case "highestRated" -> sortContext.setSort(new HighestRatedSort());
            default -> throw new CustomBadRequestException("Invalid filter.");
        }

        List<Review> pinnedReviews = listOfReviews.stream()
                .filter(r -> Boolean.TRUE.equals(r.getPinned())) // Null safe
                .collect(Collectors.toList());

        List<Review> unpinnedReviews = listOfReviews.stream()
                .filter(r -> r.getPinned() == null || !r.getPinned())
                .collect(Collectors.toList());

        List<Review> sortedPinnedReviews = sortContext.sort(pinnedReviews);
        List<Review> sortedUnpinnedReviews = sortContext.sort(unpinnedReviews);

        List<Review> sortedReviews = new ArrayList<>();
        sortedReviews.addAll(sortedPinnedReviews);
        sortedReviews.addAll(sortedUnpinnedReviews);

        return ResponseEntity.ok(sortedReviews);
    }

    @Override
    public ResponseEntity<Review> update(Long userId, Review review) {

        Review dataReview = repo.findById(review.getId())
                .orElseThrow(() -> new CustomBadRequestException("Review not found"));


        //check for user in database
        boolean existsUser = communicationService.existsUser(userId);
        if (!existsUser) {
            throw new CustomUserExistsException("Invalid user id.");
        }

        //check for owner or admin
        boolean isAdmin = communicationService.isAdmin(userId);
        if (!isAdmin && !Objects.equals(review.getUserId(), userId)) {
            throw new CustomPermissionsException("User is not owner or admin.");
        }


        updateExistingBookData(dataReview, review);
        handleText(review.getText());
        handleText(review.getTitle());
        Review saved = updateReview(dataReview, review);
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates the review in the database with the one provided.
     *
     * @param dataReview The review from the repo.
     * @param review The review object passed as parameter.
     * @return The updated dataReview.
     */
    public Review updateReview(Review dataReview, Review review) {
        dataReview.setLastEditTime(LocalDate.now());
        dataReview.setText(review.getText());
        dataReview.setTitle(review.getTitle());
        dataReview.setRating(review.getRating());
        dataReview.setSpoiler(review.getSpoiler());
        dataReview.setBookNotion(review.getBookNotion());
        return repo.save(dataReview);
    }

    /**
     *  Handles the text validation.
     *
     * @param text The text to be checked.
     */
    public void handleText(String text) {
        TextHandler textHandler = new ProfanityHandler();
        textHandler.setNext(new UrlHandler());

        textHandler.handle(text);
    }

    /**
     * Updates the existing book data.
     *
     * @param dataReview The review from the database.
     * @param review The review that is passed as a parameter.
     */
    public void updateExistingBookData(Review dataReview, Review review) {
        getReportService.updateRatingAndNotion(dataReview.getBookId(),
                dataReview.getRating(),
                dataReview.getBookNotion(),
                review.getRating(),
                review.getBookNotion());
    }

    @Override
    public ResponseEntity<String> delete(Long reviewId, Long userId) {
        Review review = repo.findById(reviewId)
                .orElseThrow(() -> new CustomBadRequestException("Review not found"));

        boolean isAdmin = communicationService.isAdmin(userId); // call method for admin check from users

        //check for owner or admin
        if (Objects.equals(userId, review.getUserId()) || isAdmin) {
            deleteBookDataUpdate(review);
            repo.deleteById(reviewId);
            return ResponseEntity.ok().build();
        }

        throw new CustomPermissionsException("User is not owner or admin.");


    }

    public void deleteBookDataUpdate(Review review) {
        getReportService.removeRatingAndNotion(review.getBookId(), review.getRating(), review.getBookNotion());
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
        if (!(List.of(0, 1).contains(body))) {
            throw new CustomBadRequestException("The only accepted bodies are 0 for downvote and 1 for upvote");
        }
        Review review = get(reviewId).getBody();
        assert review != null;
        checkBody(review, body);
        repo.save(review);
        return ResponseEntity.ok("Vote added, new vote values are:\nupvotes: "
                + review.getUpvote() + "\ndownvotes: " + review.getDownvote());
    }

    /**
     * Checks whether is upvoting or downvoting a review.
     *
     * @param review The review that is being voted
     * @param body The vote, 0 for downvote and 1 for upvote
     */

    private void checkBody(Review review, Integer body) {
        if (body == 1) {
            review.upvote(review.getUpvote() + 1);
        } else {
            review.downvote(review.getDownvote() + 1);
        }
    }

    @Override
    public ResponseEntity<List<Review>> mostUpvotedReviews(Long userId) {
        if (!communicationService.existsUser(userId)) {
            throw new CustomUserExistsException("Invalid user id");
        }
        List<Review> finalRes = repo.findTop3ByUserIdOrderByUpvoteDesc(userId);
        return ResponseEntity.ok(finalRes);
    }

    @Override
    public ResponseEntity<String> pinReview(Long reviewId, Boolean body) {
        Review review = get(reviewId).getBody();
        assert review != null;
        review.setPinned(body);
        repo.save(review);
        return ResponseEntity.ok("Review pinned.");
    }
}
