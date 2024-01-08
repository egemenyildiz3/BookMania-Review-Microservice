package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ReviewServiceImplTest {

    private ReviewServiceImpl service;
    private ReviewRepository repository;

    private CommunicationServiceImpl communicationService;


    @BeforeEach
    public void setup() {
        repository = mock(ReviewRepository.class);
        communicationService = mock(CommunicationServiceImpl.class);
        service = new ReviewServiceImpl(repository,communicationService);

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
        assertEquals(res.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);

        res = service.add(null);
        assertEquals(res.getStatusCode(),HttpStatus.BAD_REQUEST);
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
        assertEquals(result.getStatusCode(),HttpStatus.FORBIDDEN);
    }

    @Test
    void updateProfanities() {
        Review review = new Review(1L,2L,10L);
        review.id(1L);
        review.userId(10L);
        review.setText("fuck");
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        var result = service.update(10L,review);
        verify(repository,never()).save(review);
        assertEquals(result.getStatusCode(),HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    void updateNull(){
        var res = service.update(1L,null);
        assertEquals(res.getStatusCode(),HttpStatus.BAD_REQUEST);
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
    void deleteInvalid() {
        Review review = new Review(1L,2L,10L);
        when(repository.existsById(1L)).thenReturn(false);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(repository).deleteById(1L);

        var result = service.delete(1L,10L);
        verify(repository).existsById(1L);
        verify(repository,never()).findById(1L);
        verify(repository,never()).deleteById(1L);
        assertEquals(result.getStatusCode(),HttpStatus.BAD_REQUEST);
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

    @Test
    void seeAllMostRecent() {
        Review r = new Review(1L,2L,10L);
        Review r2 = new Review(2L,2L,9L);
        Review r3 = new Review(3L,1L,8L);
        Review r4 = new Review(4L,2L,7L);
        Review r5 = new Review(5L,2L,6L);
        when(repository.findAll()).thenReturn(List.of(r,r2,r3,r4,r5));

        r.setTimeCreated(LocalDate.of(2003,12,27));
        r2.setTimeCreated(LocalDate.of(2020,9,27));
        r3.setTimeCreated(LocalDate.of(2020,9,30));
        r4.setTimeCreated(LocalDate.of(2019,1,27));
        r5.setTimeCreated(LocalDate.of(2020,9,26));

        List<Review> correctList = List.of(r2,r5,r4,r);

        ResponseEntity<List<Review>> reviews = service.seeAll(2L, "mostRecent");
        assertEquals(reviews.getBody(), correctList);
    }

    @Test
    void seeAllMostUpvote() {
        Review r = new Review(1L,2L,10L);
        Review r2 = new Review(2L,2L,9L);
        Review r3 = new Review(3L,1L,8L);
        Review r4 = new Review(4L,2L,7L);
        Review r5 = new Review(5L,2L,6L);
        when(repository.findAll()).thenReturn(List.of(r,r2,r3,r4,r5));

        r.setUpvote(9999L);
        r2.setUpvote(0L);
        r3.setUpvote(9999L);
        r4.setUpvote(1L);
        r5.setUpvote(10000L);

        List<Review> correctList = List.of(r5,r,r4,r2);

        ResponseEntity<List<Review>> reviews = service.seeAll(2L, "highestRated");
        assertEquals(reviews.getBody(), correctList);
    }

    @Test
    void seeAllMostRelevant() {
        Review r = new Review(1L,2L,10L);
        Review r2 = new Review(2L,2L,9L);
        Review r3 = new Review(3L,1L,8L);
        Review r4 = new Review(4L,2L,7L);
        Review r5 = new Review(5L,2L,6L);
        when(repository.findAll()).thenReturn(List.of(r,r2,r3,r4,r5));

        r.setUpvote(9999L);
        r.setDownvote(1000L);
        r2.setUpvote(0L);
        r2.setDownvote(1000L);
        r3.setUpvote(9999L);
        r3.setDownvote(0L);
        r4.setUpvote(1L);
        r4.setDownvote(0L);
        r5.setUpvote(10000L);
        r5.setDownvote(1002L);

        List<Review> correctList = List.of(r,r5,r4,r2);

        ResponseEntity<List<Review>> reviews = service.seeAll(2L, "mostRelevant");
        assertEquals(reviews.getBody(), correctList);
    }

    @Test
    void retrieveMostUpvotedTest() {
        Long userId = 17L;
        Review r1 = new Review(1L, 2L, userId);
        Review r2 = new Review(2L, 2L, userId);
        Review r3 = new Review(3L, 3L, userId);
        Review r4 = new Review(4L, 5L, 89L);
        Review r5 = new Review(5L, 5L, 78L);
        Review r6 = new Review(6L, 2L, userId);

        when(repository.findAll()).thenReturn(List.of(r1, r2, r3, r4, r5, r6));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(r1));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(2L)).thenReturn(true);
        when(repository.findById(2L)).thenReturn(Optional.of(r2));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(3L)).thenReturn(true);
        when(repository.findById(3L)).thenReturn(Optional.of(r3));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));


        when(repository.existsById(4L)).thenReturn(true);
        when(repository.findById(4L)).thenReturn(Optional.of(r4));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(5L)).thenReturn(true);
        when(repository.findById(5L)).thenReturn(Optional.of(r5));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(6L)).thenReturn(true);
        when(repository.findById(6L)).thenReturn(Optional.of(r6));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(1L, 1);
        service.addVote(3L, 1);
        service.addVote(3L, 1);
        service.addVote(3L, 1);
        service.addVote(6L, 1);
        service.addVote(6L, 1);
        service.addVote(6L, 1);
        service.addVote(6L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);
        service.addVote(4L, 1);

        List<Review> corrList = new ArrayList<>();
        corrList.add(r1);
        corrList.add(r6);
        corrList.add(r3);
        assertEquals(service.mostUpvotedReviews(userId).getBody(), corrList);
    }
}