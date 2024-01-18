package nl.tudelft.sem.template.review.services;

import java.util.List;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomUserExistsException;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GetReportServiceImpl implements GetReportService {

    private final BookDataRepository bookDataRepository;
    private final CommunicationServiceImpl communicationService;
    private final CommentService commentService;

    /**
     * Constructor for the GetReportServiceImpl.
     *
     * @param bdr - the bookDataRepository
     * @param cs - the communicationService
     * @param co - the commentService
     */
    public GetReportServiceImpl(BookDataRepository bdr,
                                CommunicationServiceImpl cs,
                                CommentService co) {
        this.bookDataRepository = bdr;
        this.communicationService = cs;
        this.commentService = co;
    }

    @Override
    public ResponseEntity<BookData> getReport(Long bookId, Long userId, String info) {
        if (userId == null) {
            throw new CustomBadRequestException("UserId cannot be null");
        }
        if (bookId == null) {
            throw new CustomBadRequestException("bookId cannot be null");
        }
        if (info == null) {
            throw new CustomBadRequestException("info cannot be null");
        }
        if (!communicationService.existsUser(userId)) {
            throw new CustomUserExistsException("User doesn't exist");
        }

        if (!communicationService.existsBook(bookId)) {
            throw new CustomBadRequestException("Book doesn't exist");
        }
        if (!(communicationService.isAuthorIntegration(bookId, userId) || communicationService.isAdmin(userId))) {
            throw new CustomBadRequestException("User is not authorised to view this BookData");
        }

        // If the bookData doesn't exist yet, then create an empty one for the repository, and work with it later
        if (!bookDataRepository.existsById(bookId)) {
            return createBookDataInRepository(bookId);
        }

        BookData result = initializeLazyObjectFromDatabase(bookDataRepository.getOne(bookId));

        switch (info) {
            case "report" -> {
                return getReportFromBook(bookId, result);
            }
            case "rating" -> {
                return getRatingFromBook(bookId, result);
            }
            case "interactions" -> {
                return getInteractionsFromBook(result);
            }
            default -> throw new CustomBadRequestException("Invalid info type");
        }
    }

    private ResponseEntity<BookData> getInteractionsFromBook(BookData result) {
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<BookData> getRatingFromBook(Long bookId, BookData result) {
        BookData rating = new BookData(bookId);
        rating.setAvrRating(result.getAvrRating());
        return ResponseEntity.ok(rating);
    }

    private ResponseEntity<BookData> getReportFromBook(Long bookId, BookData result) {

        List<Long> comments = commentService.findMostUpvotedCommentAndReview(bookId);
        result.setMostUpvotedReview(comments.get(0));
        result.setMostUpvotedComment(comments.get(1));

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<BookData> addRatingAndNotion(Long bookId, Long rating, Review.BookNotionEnum notion) {
        if (!bookDataRepository.existsById(bookId)) {
            createBookDataInRepository(bookId);
        }

        BookData bd = initializeLazyObjectFromDatabase(bookDataRepository.getOne(bookId));
        calculateRatingAndNotion(bd, rating, notion, true);

        BookData saved = bookDataRepository.save(bd);

        return ResponseEntity.ok(saved);
    }

    private void calculateRatingAndNotion(BookData bd, Long rating, Review.BookNotionEnum notion, boolean add) {
        int sign = add ? 1 : -1;
        int totalReviews = bd.getNegativeRev() + bd.getPositiveRev() + bd.getNeutralRev();
        double totalRating = bd.getAvrRating() * totalReviews;
        totalRating += rating * sign;
        totalReviews += sign;
        if (totalReviews == 0) {
            bd.setAvrRating(0.0);
        } else {
            bd.setAvrRating(totalRating / totalReviews);
        }

        switch (notion) {
            case NEUTRAL -> bd.setNeutralRev(bd.getNeutralRev() + sign);
            case NEGATIVE -> bd.setNegativeRev(bd.getNegativeRev() + sign);
            default -> bd.setPositiveRev(bd.getPositiveRev() + sign);
        }
    }

    @Override
    public ResponseEntity<BookData> removeRatingAndNotion(Long bookId, Long rating, Review.BookNotionEnum notion) {
        // If you're trying to delete a review, and there is no bookData object yet
        // then something has gone horribly wrong
        if (!bookDataRepository.existsById(bookId)) {
            throw new CustomBadRequestException("BookData doesn't exist yet");
        }

        BookData bd = initializeLazyObjectFromDatabase(bookDataRepository.getOne(bookId));
        calculateRatingAndNotion(bd, rating, notion, false);

        BookData saved = bookDataRepository.save(bd);

        return ResponseEntity.ok(saved);
    }

    @Override
    public ResponseEntity<BookData> updateRatingAndNotion(Long bookId, Long oldRating, Review.BookNotionEnum oldNotion,
                                                          Long newRating, Review.BookNotionEnum newNotion) {
        removeRatingAndNotion(bookId, oldRating, oldNotion);
        return addRatingAndNotion(bookId, newRating, newNotion);
    }

    /**
     * Creates an empty BookData object in the repository.
     *
     * @param bookId - the id of the book to create for
     * @return The object as saved in the database
     */
    public ResponseEntity<BookData> createBookDataInRepository(Long bookId) {
        if (bookId == null) {
            throw new CustomBadRequestException("BookId cannot be null");
        }
        if (bookDataRepository.existsById(bookId)) {
            throw new CustomBadRequestException("BookData already exists");
        }
        if (!communicationService.existsBook(bookId)) {
            throw new CustomBadRequestException("Book doesn't exist");
        }

        BookData bd = new BookData(bookId);
        bd.setAvrRating(0.0);
        bd.setPositiveRev(0);
        bd.setNeutralRev(0);
        bd.setNegativeRev(0);

        bd = bookDataRepository.save(bd);

        return ResponseEntity.ok(bd);
    }

    private BookData initializeLazyObjectFromDatabase(BookData bd) {
        BookData result = new BookData(bd.getBookId());
        result.setPositiveRev(bd.getPositiveRev());
        result.setNegativeRev(bd.getNegativeRev());
        result.setAvrRating(bd.getAvrRating());
        result.setPositiveRev(bd.getPositiveRev());
        result.setNeutralRev(bd.getNeutralRev());
        // No need to set the most upvoted comment/review since they are null from the database
        return result;
    }
}
