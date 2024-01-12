package nl.tudelft.sem.template.example.services;

import java.util.*;
import java.time.*;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.example.Exceptions.CustomBadRequestException;
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
    public ResponseEntity<Comment> add(Comment comment) {

        if (comment == null || !reviewRepository.existsById(comment.getReviewId())) {
            return ResponseEntity.badRequest().build();
        }
        if (checkProfanities(comment.getText())) {
            return ResponseEntity.badRequest().build();
        }
        Review review = reviewRepository.findById(comment.getReviewId()).get();
        comment.setId(0L);
        comment.setDownvote(0L);
        comment.setUpvote(0L);
        comment.setTimeCreated(LocalDate.now());
        comment.setReportList(new ArrayList<>());
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
                .filter(c -> c.getReviewId().equals(reviewId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(comments);

    }

    @Override
    public ResponseEntity<Comment> update(Long userId, Comment comment) {
        if (comment == null || !Objects.equals(comment.getUserId(), userId) || !repository.existsById(comment.getId())) {
            return ResponseEntity.badRequest().header("blah").build();
        }

        if (checkProfanities(comment.getText())) {
            return ResponseEntity.badRequest().build();
        }
        Comment dataCom = repository.getOne(comment.getId());
        dataCom.setText(comment.getText());
        Comment updated = repository.save(dataCom);
        return ResponseEntity.ok(updated);
    }

    @Override
    public ResponseEntity<String> delete(Long commentId, Long userId) {
        if (!repository.existsById(commentId)) {
            return ResponseEntity.badRequest().build();
        }
        Comment comment = repository.findById(commentId).get();
        Review rev = reviewRepository.getOne(comment.getReviewId());
        if (Objects.equals(userId, comment.getUserId())) {
            //repository.deleteById(commentId);
            rev.getCommentList().remove(comment);
            reviewRepository.save(rev);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<Long> findMostUpvotedComment(Long bookId){
        List<Comment> allComments = repository.findAll();
        Optional<Comment> result =
        allComments.stream().filter(x -> {
            Review associatedReview = reviewRepository.getOne(x.getReviewId());
            if(associatedReview == null) return false;
            return Objects.equals(associatedReview.getBookId(), bookId);
        })
                .max(Comparator.comparingLong(Comment::getUpvote));
        if (result.isEmpty()){
            throw new CustomBadRequestException("No comments found");
        }
        return ResponseEntity.ok(result.get().getId());
    }

    @Override
    public ResponseEntity<String> addVote(Long commentId, Integer body) {
        if(!repository.existsById(commentId) || get(commentId).getBody() == null) {
            return ResponseEntity.badRequest().body("Comment id does not exist.");
        }

        if (!(List.of(0, 1).contains(body))) {
            return ResponseEntity.badRequest().body("The only accepted bodies are 0 for downvote and 1 for upvote.");
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
        return ResponseEntity.ok("Vote added, new vote values are:\nupvotes: " +
                ((comment.getUpvote() == null) ? 0 : comment.getUpvote()) +
                "\ndownvotes: " + ((comment.getDownvote() == null) ? 0 : comment.getDownvote()));
    }
}
