package nl.tudelft.sem.template.review.services;

import nl.tudelft.sem.template.review.Exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.services.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class CommentServiceImplTest {

    private CommentServiceImpl service;
    private CommentRepository commentRepository;
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setup() {
        this.commentRepository = mock(CommentRepository.class);
        this.reviewRepository = mock(ReviewRepository.class);
        this.service = new CommentServiceImpl(this.commentRepository, this.reviewRepository);
    }

    @Test
    void testCheckProfanitiesTrue() {
        assertTrue(CommentServiceImpl.checkProfanities("This review is fucking trash"));
    }

    @Test
    void testCheckProfanitiesFalse() {
        assertFalse(CommentServiceImpl.checkProfanities("This review is great"));
    }

    @Test
    void testAdd() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(3L, 2L, 10L, "comment");
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.existsById(2L)).thenReturn(true);
        when(reviewRepository.findById(2L)).thenReturn(Optional.of(review));
        var result = service.add(comment);
        verify(reviewRepository).save(review);
        verify(reviewRepository).existsById(2L);
        verify(reviewRepository).findById(2L);
        assertEquals(result.getBody(), comment);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testAddProfanities() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(3L, 2L, 10L, "fuck");
        when(reviewRepository.save(review)).thenReturn(review);
        var result = service.add(comment);
        verify(reviewRepository, never()).save(review);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetValid() {
        Comment comment = new Comment(1L, 2L, 10L, "comment");
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        var result = service.get(1L);
        verify(commentRepository).findById(1L);
        verify(commentRepository).existsById(1L);
        assertEquals(result.getBody(), comment);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testGetInvalid() {
        when(commentRepository.existsById(1L)).thenReturn(false);
        var result = service.get(1L);
        verify(commentRepository, never()).findById(1L);
        verify(commentRepository).existsById(1L);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateOwner() {
        Comment comment = new Comment(1L, 2L, 10L, "comment");
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.getOne(1L)).thenReturn(comment);
        var result = service.update(2L, comment);
        verify(commentRepository).save(comment);
        verify(commentRepository).existsById(1L);
//        verify(commentRepository).findById(1L);
        assertEquals(result.getBody(), comment);
        comment.text("great");
        result = service.update(2L, comment);
        assertEquals(result.getBody(), comment);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testUpdateOwnerProfanities() {
        Comment comment = new Comment(1L, 2L, 10L, "comment");
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.getOne(1L)).thenReturn(comment);
        var result = service.update(2L, comment);
        verify(commentRepository).save(comment);
        verify(commentRepository).existsById(1L);
//        verify(commentRepository).findById(1L);
        assertEquals(result.getBody(), comment);
        comment.text("fuck");
        result = service.update(2L, comment);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateNotOwner() {
        Comment comment = new Comment(1L, 2L, 10L, "comment");
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.save(comment)).thenReturn(comment);
        var result = service.update(3L, comment);
        verify(commentRepository, never()).save(comment);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDeleteOwner() {
        Review review = new Review(3L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(1L, 5L, 2L, "comment");
        comment.setReviewId(review.getId());
        review.addCommentListItem(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.getOne(3L)).thenReturn(review);
        var result = service.delete(1L, 2L);
        verify(commentRepository).existsById(1L);
        verify(commentRepository).findById(1L);
        verify(reviewRepository).save(review);
        assertTrue(review.getCommentList().isEmpty());
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testDeleteNotOwner() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(3L, 2L, 10L, "comment");
        comment.setReviewId(review.getId());
        review.addCommentListItem(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(reviewRepository.save(review)).thenReturn(review);
        var result = service.delete(1L, 3L);
        verify(commentRepository).existsById(1L);
        verify(commentRepository).findById(1L);
        verify(reviewRepository, never()).save(review);
        assertTrue(review.getCommentList().contains(comment));
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetAll() {
        Review r1 = new Review(17L, 1L, 1L, "Review", "review", 5L);
        Review r2 = new Review(13L, 2L, 2L, "Review", "review", 5L);
        Comment c1 = new Comment(1L, 17L, 1L, "comment");
        Comment c2 = new Comment(2L, 17L, 1L, "comment");
        Comment c3 = new Comment(3L, 13L, 1L, "comment");
        Comment c4 = new Comment(4L, 17L, 1L, "comment");
        Comment c5 = new Comment(5L, 17L, 1L, "comment");
        Comment c6 = new Comment(6L, 17L, 1L, "comment");

        when(reviewRepository.existsById(17L)).thenReturn(true);
        when(reviewRepository.existsById(2L)).thenReturn(false);
        when(commentRepository.findAll()).thenReturn(List.of(c1,c2,c3,c4,c5,c6));

        List<Comment> correctList = List.of(c1,c2,c4,c5,c6);
        ResponseEntity<List<Comment>> result = service.getAll(17L);
        ResponseEntity<List<Comment>> failResult = service.getAll(2L);

        assertEquals(result.getBody(), correctList);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(failResult.getStatusCode(), HttpStatus.BAD_REQUEST);

    }
    @Test
    void findMostUpvotedComment() {
        Review reviewOne = new Review(5L, 15L, 15L, "rev1", "rev1t", 5L);
        Review reviewTwo = new Review(10L, 10L, 15L, "rev1", "rev1t", 5L);
        Comment commentOne = new Comment(1L, 5L, 15L, "a");
        Comment commentTwo = new Comment(2L, 10L, 20L, "b");
        Comment commentThree = new Comment(3L, 10L, 25L, "c");
        commentOne.setUpvote(25L);
        commentTwo.setUpvote(15L);
        commentThree.setUpvote(20L);
        List<Comment> comments = List.of(new Comment[] {commentOne,commentTwo, commentThree});
        when(commentRepository.findAll()).thenReturn(comments);
        when(reviewRepository.getOne(5L)).thenReturn(reviewOne);
        when(reviewRepository.getOne(10L)).thenReturn(reviewTwo);
        
        var result = service.findMostUpvotedComment(10L);

        assertEquals(result.getBody(), 3L);
    }

    @Test
    void findMostUpvotedCommentNoResults(){
        Review reviewOne = new Review(5L, 15L, 15L, "rev1", "rev1t", 5L);
        Review reviewTwo = new Review(10L, 10L, 15L, "rev1", "rev1t", 5L);
        Comment commentOne = new Comment(1L, 5L, 15L, "a");
        Comment commentTwo = new Comment(2L, 10L, 20L, "b");
        Comment commentThree = new Comment(3L, 10L, 25L, "c");
        commentOne.setUpvote(25L);
        commentTwo.setUpvote(15L);
        commentThree.setUpvote(20L);
        List<Comment> comments = List.of(new Comment[] {commentOne,commentTwo, commentThree});
        when(commentRepository.findAll()).thenReturn(comments);
        when(reviewRepository.getOne(5L)).thenReturn(reviewOne);
        when(reviewRepository.getOne(10L)).thenReturn(reviewTwo);
        assertThrows(CustomBadRequestException.class, () -> service.findMostUpvotedComment(11L));
    }

    @Test
    void wrongBodyVoteTest() {
        Review r1 = new Review(17L, 1L, 1L, "Review", "review", 5L);
        Comment c1 = new Comment(1L, 17L, 1L, "comment");

        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ResponseEntity<String> res1 = service.addVote(1L, 7);

        assertEquals(res1.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void upvoteAndDownvoteTest() {
        Review r1 = new Review(17L, 1L, 1L, "Review", "review", 5L);
        Comment c1 = new Comment(1L, 17L, 1L, "comment");

        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> res1 = service.addVote(1L, 1);
        ResponseEntity<String> res2 = service.addVote(1L, 0);

        assertEquals(res1.getStatusCode(), HttpStatus.OK);
        assertEquals(res2.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void multipleUpvoteDownvoteTest() {
        Review r1 = new Review(17L, 1L, 1L, "Review", "review", 5L);
        Comment c1 = new Comment(1L, 17L, 1L, "comment");

        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(1L, 1);

        ResponseEntity<String> res1 = service.addVote(1L, 1);

        service.addVote(1L, 0);
        service.addVote(1L, 0);
        service.addVote(1L, 0);
        service.addVote(1L, 0);
        service.addVote(1L, 0);

        ResponseEntity<String> res2 = service.addVote(1L, 0);

        assertEquals(res1.getStatusCode(), HttpStatus.OK);
        assertEquals(res2.getStatusCode(), HttpStatus.OK);
        assertEquals(5, c1.getUpvote());
        assertEquals(6, c1.getDownvote());
    }
}