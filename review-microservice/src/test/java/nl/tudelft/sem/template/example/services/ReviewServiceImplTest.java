package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ReviewServiceImplTest {

    private ReviewServiceImpl service;
    private ReviewRepository repository;



    @BeforeEach
    public void setup() {
        repository = mock(ReviewRepository.class);
        service = new ReviewServiceImpl(repository);

    }

    @Test
    void add() {
        Review review = new Review(1L,2L,10L);
        when(repository.save(review)).thenReturn(review);
        var result = service.add(review);
        verify(repository).save(review);
        assertEquals(result.getBody(),review);
        Review r1 = new Review(1L,2L,10L);
        r1.text("FUCK");
        when(repository.save(r1)).thenReturn(r1);
        var res = service.add(r1);
        verify(repository,never()).save(r1);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void checkProfanities() {
        assertTrue(ReviewServiceImpl.checkProfanities("This book is fucking bad"));
        assertFalse(ReviewServiceImpl.checkProfanities("This book is so fun"));

    }

    @Test
    void getValid() {
        Review review = new Review(1L,2L,10L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        var result = service.get(1L);
        verify(repository).findById(1L);
        verify(repository).existsById(1L);
        assertEquals(result.getBody(),review);

    }

    @Test
    void getInvalid(){
        when(repository.existsById(1L)).thenReturn(false);
        var res = service.get(1L);
        verify(repository,never()).findById(1L);
        verify(repository).existsById(1L);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateOwner() {
        Review review = new Review(1L,2L,10L);
        review.id(1L);
        review.userId(10L);
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        var result = service.update(10L,review);
        verify(repository).save(review);
        assertEquals(result.getBody(),review);
        review.text("hahaha");
        result = service.update(10L,review);
        assertEquals(result.getBody(),review);
    }
    @Test
    void updateNotOwner() {
        Review review = new Review(1L,2L,10L);
        review.id(1L);
        review.userId(10L);
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        var result = service.update(9L,review);
        verify(repository,never()).save(review);
        assertEquals(result.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    void delete() {
        Review review = new Review(1L,2L,10L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(repository).deleteById(1L);

        var result = service.delete(1L,10L);
        verify(repository).existsById(1L);
        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
        assertEquals(result.getStatusCode(),HttpStatus.OK);
    }

    @Test
    void testAddSpoiler() {
        Long reviewId = 1L;
        Review review = new Review(1L,2L,10L);

        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = service.addSpoiler(reviewId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(review.getSpoiler());
        verify(repository, times(1)).save(review);
    }

    @Test
    void testAddUpvote() {
        Long reviewId = 1L;
        Integer upvote = 1;
        Review review = new Review(1L,2L,10L);

        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = service.addVote(reviewId, upvote);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(review.getUpvote(), 1);
        verify(repository, times(1)).save(review);
    }

    @Test
    void testAddDownvote() {
        Long reviewId = 1L;
        Integer downvote = 0;
        Review review = new Review(1L,2L,10L);

        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = service.addVote(reviewId, downvote);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNull(review.getUpvote());
        assertEquals(review.getDownvote(), 1);
        verify(repository, times(1)).save(review);
    }
}