package nl.tudelft.sem.template.review.services;

import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import nl.tudelft.sem.template.review.exceptions.CustomUserExistsException;
import nl.tudelft.sem.template.review.repositories.CommentRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
class CommentServiceImplTest {

    private CommentServiceImpl service;
    private CommunicationServiceImpl communicationService;
    private CommentRepository commentRepository;
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setup() {
        this.commentRepository = mock(CommentRepository.class);
        this.reviewRepository = mock(ReviewRepository.class);
        this.communicationService = mock(CommunicationServiceImpl.class);
        this.service = new CommentServiceImpl(this.commentRepository, this.reviewRepository, this.communicationService);
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
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.existsById(2L)).thenReturn(true);
        when(reviewRepository.findById(2L)).thenReturn(Optional.of(review));
        final var result = service.add(comment);
        verify(reviewRepository).save(review);
        verify(reviewRepository).existsById(2L);
        verify(reviewRepository).findById(2L);
        assertEquals(result.getBody(), comment);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testAddNull() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = null;
        when(reviewRepository.save(review)).thenReturn(review);
        assertThrows(CustomBadRequestException.class, () -> service.add(comment));
        verify(reviewRepository, never()).save(review);
    }

    @Test
    void testAddInvalidReview() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(3L, 2L, 10L, "comment");
        when(reviewRepository.existsById(2L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class, () -> service.add(comment));
        verify(reviewRepository, never()).save(review);
    }

    @Test
    void testAddInvalidUser() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(3L, 2L, 10L, "comment");
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.existsById(2L)).thenReturn(true);
        when(communicationService.existsUser(10L)).thenReturn(false);
        assertThrows(CustomUserExistsException.class, () -> service.add(comment));
        verify(reviewRepository, never()).save(review);
    }

    @Test
    void testAddProfanities() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(3L, 2L, 10L, "fuck");
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.existsById(2L)).thenReturn(true);
        when(communicationService.existsUser(10L)).thenReturn(true);
        assertThrows(CustomProfanitiesException.class, () -> service.add(comment));
        verify(reviewRepository, never()).save(review);
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
        assertThrows(CustomBadRequestException.class, () -> service.get(1L));
        verify(commentRepository, never()).findById(1L);
        verify(commentRepository).existsById(1L);
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
        //  verify(commentRepository).findById(1L);
        assertEquals(result.getBody(), comment);
        comment.text("great");
        result = service.update(2L, comment);
        assertEquals(result.getBody(), comment);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testUpdateNull() {
        Comment comment = null;
        assertThrows(CustomBadRequestException.class, () -> service.update(2L, comment));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void testUpdateInvalidComment() {
        Comment comment = new Comment(1L, 2L, 10L, "comment");
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.existsById(1L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class, () -> service.update(2L, comment));
        verify(commentRepository, never()).save(comment);
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
        //  verify(commentRepository).findById(1L);
        assertEquals(result.getBody(), comment);
        comment.text("fuck");
        assertThrows(CustomProfanitiesException.class, () -> service.update(2L, comment));
    }

    @Test
    void testUpdateNotOwner() {
        Comment comment = new Comment(1L, 2L, 10L, "comment");
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.save(comment)).thenReturn(comment);
        assertThrows(CustomBadRequestException.class, () -> service.update(3L, comment));
        verify(commentRepository, never()).save(comment);
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
        final var result = service.delete(1L, 2L);
        verify(commentRepository).existsById(1L);
        verify(commentRepository).findById(1L);
        verify(reviewRepository).save(review);
        assertTrue(review.getCommentList().isEmpty());
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testDeleteNotOwner() {
        Review review = new Review(2L, 5L, 10L, "Review", "review", 5L);
        Comment comment = new Comment(1L, 2L, 10L, "comment");
        comment.setReviewId(review.getId());
        review.addCommentListItem(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(reviewRepository.save(review)).thenReturn(review);
        assertThrows(CustomPermissionsException.class, () -> service.delete(1L, 5L));

        verify(commentRepository).existsById(1L);
        verify(commentRepository).findById(1L);
        verify(reviewRepository, never()).save(review);
        assertTrue(review.getCommentList().contains(comment));
    }

    @Test
    void testGetAll() {
        Comment c1 = new Comment(1L, 17L, 1L, "comment");
        Comment c2 = new Comment(2L, 17L, 1L, "comment");
        Comment c3 = new Comment(3L, 13L, 1L, "comment");
        Comment c4 = new Comment(4L, 17L, 1L, "comment");
        Comment c5 = new Comment(5L, 17L, 1L, "comment");
        Comment c6 = new Comment(6L, 17L, 1L, "comment");

        when(reviewRepository.existsById(17L)).thenReturn(true);
        when(reviewRepository.existsById(2L)).thenReturn(false);
        when(commentRepository.findAll()).thenReturn(List.of(c1, c2, c3, c4, c5, c6));

        List<Comment> correctList = List.of(c1, c2, c4, c5, c6);
        ResponseEntity<List<Comment>> result = service.getAll(17L);

        assertEquals(result.getBody(), correctList);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertThrows(CustomBadRequestException.class, () -> service.getAll(2L));

    }

    @Test
    void findMostUpvotedComment() {
        final Review reviewOne = new Review(5L, 15L, 15L, "rev1", "rev1t", 5L);
        final Review reviewTwo = new Review(10L, 10L, 15L, "rev1", "rev1t", 5L);
        Comment commentOne = new Comment(1L, 5L, 15L, "a");
        Comment commentTwo = new Comment(2L, 10L, 20L, "b");
        Comment commentThree = new Comment(3L, 10L, 25L, "c");
        commentOne.setUpvote(25L);
        commentTwo.setUpvote(15L);
        commentThree.setUpvote(20L);
        List<Comment> comments = List.of(new Comment[] {commentOne, commentTwo, commentThree});
        when(commentRepository.findAll()).thenReturn(comments);
        when(reviewRepository.getOne(5L)).thenReturn(reviewOne);
        when(reviewRepository.getOne(10L)).thenReturn(reviewTwo);
        
        var result = service.findMostUpvotedComment(10L);

        assertEquals(result.getBody(), 3L);
    }

    @Test
    void findMostUpvotedCommentNoResults() {
        final Review reviewOne = new Review(5L, 15L, 15L, "rev1", "rev1t", 5L);
        final Review reviewTwo = new Review(10L, 10L, 15L, "rev1", "rev1t", 5L);
        Comment commentOne = new Comment(1L, 5L, 15L, "a");
        Comment commentTwo = new Comment(2L, 10L, 20L, "b");
        Comment commentThree = new Comment(3L, 10L, 25L, "c");
        commentOne.setUpvote(25L);
        commentTwo.setUpvote(15L);
        commentThree.setUpvote(20L);
        List<Comment> comments = List.of(new Comment[] {commentOne, commentTwo, commentThree});
        when(commentRepository.findAll()).thenReturn(comments);
        when(reviewRepository.getOne(5L)).thenReturn(reviewOne);
        when(reviewRepository.getOne(10L)).thenReturn(reviewTwo);
        assertNull(service.findMostUpvotedComment(5L).getBody());
    }

    @Test
    void wrongBodyVoteTest() {
        Comment c1 = new Comment(1L, 17L, 1L, "comment");

        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThrows(CustomBadRequestException.class, () -> service.addVote(1L, 7));
    }

    @Test
    void upvoteAndDownvoteTest() {
        Comment c1 = new Comment(1L, 17L, 1L, "comment");

        c1.setDownvote(0L);
        c1.setUpvote(0L);

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
        Comment c1 = new Comment(1L, 17L, 1L, "comment");

        c1.setDownvote(0L);
        c1.setUpvote(0L);

        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(c1));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(1L, 1);

        final ResponseEntity<String> res1 = service.addVote(1L, 1);

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