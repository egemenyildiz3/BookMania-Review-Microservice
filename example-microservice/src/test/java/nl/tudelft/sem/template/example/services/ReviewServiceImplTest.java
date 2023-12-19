package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.RESTcontrollers.ReviewController;
import nl.tudelft.sem.template.example.model.Review;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

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
        Review review = new Review();
        when(repository.save(review)).thenReturn(review);
        var result = service.add(review);
        verify(repository).save(review);
        assertEquals(result.getBody(),review);
        Review r1 = new Review();
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
        Review review = new Review();
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
        Review review = new Review();
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
        Review review = new Review();
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
    }
}