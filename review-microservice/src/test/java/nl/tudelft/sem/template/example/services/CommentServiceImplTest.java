package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.CommentRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        Comment comment = new Comment(3L, null);
        Review review = new Review(2L, 5L, 10L);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewRepository.existsById(2L)).thenReturn(true);
        when(reviewRepository.findById(2L)).thenReturn(Optional.of(review));
        var result = service.add(1L, 2L, comment);
        verify(reviewRepository).save(review);
        verify(reviewRepository).existsById(2L);
        verify(reviewRepository).findById(2L);
        assertEquals(result.getBody(), comment);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testAddProfanities() {
        Comment comment = new Comment(3L, null);
        comment.text("fuck");
        Review review = new Review(2L, 5L, 10L);
        when(reviewRepository.save(review)).thenReturn(review);
        var result = service.add(1L, 2L, comment);
        verify(reviewRepository, never()).save(review);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testGetValid() {
        Comment comment = new Comment(1L, 2L);
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
        Comment comment = new Comment(1L, 2L);
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        var result = service.update(2L, comment);
        verify(commentRepository).save(comment);
        verify(commentRepository).existsById(1L);
        verify(commentRepository).findById(1L);
        assertEquals(result.getBody(), comment);
        comment.text("great");
        result = service.update(2L, comment);
        assertEquals(result.getBody(), comment);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testUpdateOwnerProfanities() {
        Comment comment = new Comment(1L, 2L);
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        var result = service.update(2L, comment);
        verify(commentRepository).save(comment);
        verify(commentRepository).existsById(1L);
        verify(commentRepository).findById(1L);
        assertEquals(result.getBody(), comment);
        comment.text("fuck");
        result = service.update(2L, comment);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testUpdateNotOwner() {
        Comment comment = new Comment(1L, 2L);
        comment.id(1L);
        comment.userId(2L);
        when(commentRepository.save(comment)).thenReturn(comment);
        var result = service.update(3L, comment);
        verify(commentRepository, never()).save(comment);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void testDeleteOwner() {
        Comment comment = new Comment(1L, 2L);
        Review review = new Review(3L, 5L, 10L);
        comment.setReview(review);
        review.addCommentListItem(comment);
        when(commentRepository.existsById(1L)).thenReturn(true);
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(reviewRepository.save(review)).thenReturn(review);
        var result = service.delete(1L, 2L);
        verify(commentRepository).existsById(1L);
        verify(commentRepository).findById(1L);
        verify(reviewRepository).save(review);
        assertTrue(review.getCommentList().isEmpty());
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testDeleteNotOwner() {
        Comment comment = new Comment(1L, 2L);
        Review review = new Review(3L, 5L, 10L);
        comment.setReview(review);
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
}