package nl.tudelft.sem.template.review.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.domain.textcheck.ProfanityHandler;
import nl.tudelft.sem.template.review.domain.textcheck.TextHandler;
import nl.tudelft.sem.template.review.domain.textcheck.UrlHandler;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomUserExistsException;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final ReviewRepository reviewRepository;
    private final CommunicationServiceImpl communicationService;

    /**
     * Initializes a new instance of the class with the provided dependencies.
     *
     * @param repository The comment repository instance for data storage and retrieval
     * @param reviewRepository The review repository instance for data storage and retrieval
     * @param communicationService The communication service instance for using the other teams' APIs
     */

    public CommentServiceImpl(CommentRepository repository, ReviewRepository reviewRepository,
                              CommunicationServiceImpl communicationService) {
        this.reviewRepository = reviewRepository;
        this.repository = repository;
        this.communicationService = communicationService;
    }

    @Override
    public ResponseEntity<Comment> add(Comment comment) {

        if (comment == null) {
            throw new CustomBadRequestException("Comment cannot be null.");
        }
        if (!reviewRepository.existsById(comment.getReviewId())) {
            throw new CustomBadRequestException("Invalid review id.");
        }
        if (!communicationService.existsUser(comment.getUserId())) {
            throw new CustomUserExistsException("Invalid user id.");
        }

        TextHandler textHandler = new ProfanityHandler();
        textHandler.setNext(new UrlHandler());

        textHandler.handle(comment.getText());

        comment.setId(0L);
        comment.setDownvote(0L);
        comment.setUpvote(0L);
        comment.setTimeCreated(LocalDate.now());
        comment.setReportList(new ArrayList<>());

        Review review = reviewRepository.findById(comment.getReviewId()).get();

        review.addCommentListItem(comment);
        reviewRepository.save(review);
        return ResponseEntity.ok(review.getCommentList().get(review.getCommentList().size() - 1));
    }



    @Override
    public ResponseEntity<Comment> get(Long commentId) {
        if (!repository.existsById(commentId)) {
            throw new CustomBadRequestException("Invalid comment id.");
        }
        Comment comment = new Comment(1L,2L,3L,"check");
        if (repository.findById(commentId).isPresent()) {
            comment = repository.findById(commentId).get();
        }
        return ResponseEntity.ok(comment);
    }

    @Override
    public ResponseEntity<List<Comment>> getAll(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new CustomBadRequestException("Invalid review id.");
        }

        List<Comment> comments = repository.findAll().stream()
                .filter(c -> c.getReviewId().equals(reviewId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);

    }

    @Override
    public ResponseEntity<Comment> update(Long userId, Comment comment) {
        if (comment == null) {
            throw new CustomBadRequestException("Comment cannot be null");
        }
        Comment dataCom = get(comment.getId()).getBody();
        if (!Objects.equals(dataCom.getUserId(), userId)) {
            throw new CustomBadRequestException("User id does not match");
        }
        if(!communicationService.existsUser(userId)) {
            throw new CustomUserExistsException("Invalid user id");
        }

        TextHandler textHandler = new ProfanityHandler();
        textHandler.setNext(new UrlHandler());

        textHandler.handle(comment.getText());

        dataCom.setText(comment.getText());
        Comment updated = repository.save(dataCom);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<String> delete(Long commentId, Long userId) {
        Comment comment = get(commentId).getBody();

        Review rev = reviewRepository.getOne(comment.getReviewId());
        if (Objects.equals(userId, comment.getUserId())) {
            //repository.deleteById(commentId);
            rev.getCommentList().remove(comment);
            reviewRepository.save(rev);
            return ResponseEntity.ok().build();
        }
        throw new CustomPermissionsException("User is not owner or admin.");
    }

    /**
     * Finds the most upvoted comment for a book.
     *
     * @param bookId - The book whose comments to look for
     * @return - The id of the most upvoted comment of null if no comment was found
     */
    public ResponseEntity<Long> findMostUpvotedComment(Long bookId) {
        List<Comment> allComments = repository.findAll();

        Optional<Comment> result = allComments.stream().filter(x -> {
            Review associatedReview = reviewRepository.getOne(x.getReviewId());
            return Objects.equals(associatedReview.getBookId(), bookId);
        })
                .max(Comparator.comparingLong(Comment::getUpvote));

        return result.map(comment -> ResponseEntity.ok(comment.getId()))
                .orElseGet(() -> ResponseEntity.badRequest().body(null));

    }

    @Override
    public ResponseEntity<String> addVote(Long commentId, Integer body) {
        if (!(List.of(0, 1).contains(body))) {
            throw new CustomBadRequestException("The only accepted bodies are 0 for downvote and 1 for upvote");
        }
        Comment comment = get(commentId).getBody();
        assert comment != null;
        checkBody(comment, body);
        repository.save(comment);
        return ResponseEntity.ok("Vote added, new vote values are:\nupvotes: "
                + comment.getUpvote() + "\ndownvotes: " + comment.getDownvote());
    }

    /**
     * Checks whether is upvoting or downvoting a comment.
     *
     * @param comment The comment that is being voted
     * @param body The vote, 0 for downvote and 1 for upvote
     */

    private void checkBody(Comment comment, Integer body) {
        if (body == 1) {
            comment.upvote(comment.getUpvote() + 1);
        } else {
            comment.downvote(comment.getDownvote() + 1);
        }
    }
}
