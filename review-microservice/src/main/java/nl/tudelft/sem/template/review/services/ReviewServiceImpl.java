package nl.tudelft.sem.template.review.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import nl.tudelft.sem.template.review.domain.review.filter.HighestRatedFilter;
import nl.tudelft.sem.template.review.domain.review.filter.MostRecentFilter;
import nl.tudelft.sem.template.review.domain.review.filter.MostRelevantFilter;
import nl.tudelft.sem.template.review.domain.review.filter.ReviewFilter;
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


    private static final List<String> profanities =
            Arrays.asList("fuck", "shit", "motherfucker", "bastard", "cunt", "bitch");

    /**
     * Gets the correct filter that is needed.
     *
     * @param type The needed filter
     * @return The needed implementation of
     */
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
        if (review == null) {
            throw new CustomBadRequestException("Review cannot be null.");
        }
        boolean existsBook = communicationService.existsBook(review.getBookId());
        if (!existsBook) {
            throw new CustomBadRequestException("Invalid book id.");
        }


        boolean existsUser = communicationService.existsUser(review.getUserId());
        if (!existsUser) {
            throw new CustomBadRequestException("Invalid user id.");
        }


        if (checkProfanities(review.getText())) {
            throw new CustomProfanitiesException("Profanities detected in text. Please remove them.");
        }
        getReportService.addRatingAndNotion(review.getBookId(), review.getRating(), review.getBookNotion());

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

    /**
     * Checks if the given string has profanities in it.
     *
     * @param s The string that is checked.
     * @return Whether there are profanities in the string
     */
    public static boolean checkProfanities(String s) {
        if (s != null) {
            for (String p : profanities) {
                if (s.toLowerCase().contains(p)) {
                    return true;
                }
            }
        }
        return false;
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
        if (!Arrays.asList("mostRelevant", "mostRecent", "highestRated").contains(filter)) {
            throw new CustomBadRequestException("Invalid filter.");
        }

        ReviewFilter reviewFilter = getFilter(filter);

        List<Review> pinnedReviews = listOfReviews.stream()
                .filter(r -> Boolean.TRUE.equals(r.getPinned())) // Null safe
                .collect(Collectors.toList());

        List<Review> unpinnedReviews = listOfReviews.stream()
                .filter(r -> r.getPinned() == null || !r.getPinned())
                .collect(Collectors.toList());

        List<Review> sortedPinnedReviews = reviewFilter.filter(pinnedReviews);
        List<Review> sortedUnpinnedReviews = reviewFilter.filter(unpinnedReviews);

        List<Review> sortedReviews = new ArrayList<>();
        sortedReviews.addAll(sortedPinnedReviews);
        sortedReviews.addAll(sortedUnpinnedReviews);

        return ResponseEntity.ok(sortedReviews);
    }

    @Override
    public ResponseEntity<Review> update(Long userId, Review review) {

        if (review == null || !repo.existsById(review.getId())) {
            throw new CustomBadRequestException("Invalid review id.");
        }

        //check for user in database
        boolean existsUser = communicationService.existsUser(userId);
        if (!existsUser) {
            throw new CustomBadRequestException("Invalid user id.");
        }


        //check for owner or admin
        boolean isAdmin = communicationService.isAdmin(userId);
        if (!isAdmin && !Objects.equals(review.getUserId(), userId)) {
            throw new CustomPermissionsException("User is not owner or admin.");
        }


        if (checkProfanities(review.getText())) {
            throw new CustomProfanitiesException("Profanities detected in text. Please remove them.");
        }

        Review dataReview = repo.getOne(review.getId());
        getReportService.updateRatingAndNotion(dataReview.getBookId(),
                dataReview.getRating(),
                dataReview.getBookNotion(),
                review.getRating(),
                review.getBookNotion());


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
        if (!repo.existsById(reviewId)) {
            throw new CustomBadRequestException("Invalid review id.");
        }

        Optional<Review> optionalReview = repo.findById(reviewId);
        Review review;
        if (optionalReview.isPresent()) {
            review = optionalReview.get();
        } else {
            throw new CustomBadRequestException("Cannot find review.");
        }

        boolean isAdmin = communicationService.isAdmin(userId); // call method for admin check from users

        //check for owner or admin
        if (Objects.equals(userId, review.getUserId()) || isAdmin) {
            getReportService.removeRatingAndNotion(review.getBookId(), review.getRating(), review.getBookNotion());
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
        if (!repo.existsById(reviewId) || get(reviewId).getBody() == null) {
            return ResponseEntity.badRequest().body("Review id does not exist.");
        }

        if (!(List.of(0, 1).contains(body))) {
            return ResponseEntity.badRequest().body("The only accepted bodies are 0 for downvote and 1 for upvote.");
        }
        Review review = get(reviewId).getBody();
        assert review != null;
        if (body == 1) {
            review.upvote(review.getUpvote() + 1);
        } else {
            review.downvote(review.getDownvote() + 1);
        }
        repo.save(review);
        return ResponseEntity.ok("Vote added, new vote values are:\nupvotes: "
                + ((review.getUpvote() == null) ? 0 : review.getUpvote())
                + "\ndownvotes: " + ((review.getDownvote() == null) ? 0 : review.getDownvote()));
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
        if (!repo.existsById(reviewId) || get(reviewId).getBody() == null) {
            throw new CustomBadRequestException("Review Id not found or invalid");
        }
        Review review = get(reviewId).getBody();
        assert review != null;
        review.setPinned(body);
        repo.save(review);
        return ResponseEntity.ok("Review pinned.");
    }
}
