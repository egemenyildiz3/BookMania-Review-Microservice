package nl.tudelft.sem.template.review.services;

import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.review.services.GetReportServiceImpl;
import nl.tudelft.sem.template.review.services.ReviewServiceImpl;
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
        GetReportServiceImpl getReportService = mock(GetReportServiceImpl.class);
        when(getReportService.addRatingAndNotion(any(),any(),any())).thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));

        when(getReportService.removeRatingAndNotion(any(),any(),any())).thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        when(getReportService.updateRatingAndNotion(any(),any(),any(),any(),any())).thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        when(getReportService.createBookDataInRepository(any())).thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        service = new ReviewServiceImpl(getReportService,repository,communicationService);


    }

    @Test
    void add() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.save(review)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(true);
        var result = service.add(review);
        verify(repository).save(review);
        assertEquals(result.getBody(),review);
        assertEquals(result.getBody().getUpvote(),0L);
        assertEquals(result.getBody().getDownvote(),0L);
        assertEquals(result.getBody().getReportList().size(),0);
        assertEquals(result.getBody().getCommentList().size(),0);
        assertEquals(result.getBody().getId(),0);

        Review r1 = new Review(1L,2L,10L, "Review", "review", 5L);
        r1.text("FUCK");
        when(repository.save(r1)).thenReturn(r1);
        assertThrows(CustomProfanitiesException.class, () -> service.add(r1));
        verify(repository,never()).save(r1);


        assertThrows(CustomBadRequestException.class, () -> service.add(null));
        verify(repository,never()).save(r1);

    }

    @Test
    void addInvalidBook() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.save(review)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class, () -> service.add(review));
        verify(repository,never()).save(review);

    }
    @Test
    void addInvalidUser() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.save(review)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(false);
        when(communicationService.existsBook(2L)).thenReturn(true);
        assertThrows(CustomBadRequestException.class, () -> service.add(review));
        verify(repository,never()).save(review);

    }
    @Test
    void checkProfanities() {
        assertTrue(ReviewServiceImpl.checkProfanities("This book is fucking bad"));
        assertFalse(ReviewServiceImpl.checkProfanities("This book is so fun"));
        assertFalse(ReviewServiceImpl.checkProfanities(null));
    }

    @Test
    void getValid() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        var result = service.get(1L);
        verify(repository,times(2)).findById(1L);
        verify(repository).existsById(1L);
        assertEquals(result.getBody(),review);

    }

    @Test
    void getInvalid(){
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class, () -> service.get(1L));
        verify(repository,never()).getOne(1L);
        verify(repository).existsById(1L);
    }

    @Test
    void updateOwnerNotAdmin() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.id(1L);
        review.userId(10L);
        review.setBookNotion(Review.BookNotionEnum.NEGATIVE);
        review.setSpoiler(false);
        Review review1 = new Review(1L,2L,10L, "Rev", "rev", 3L);
        review1.setBookNotion(Review.BookNotionEnum.NEUTRAL);
        review1.setSpoiler(true);
        when(repository.save(review1)).thenReturn(review1);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.getOne(1L)).thenReturn(review1);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.isAdmin(2L)).thenReturn(false);

        var result = service.update(10L,review);
        verify(repository).save(review1);
        review.text("hahaha");
        result = service.update(10L,review);
        assertEquals(result.getBody(),review1);

        assertEquals(result.getBody().getText(),review.getText());
        assertEquals(result.getBody().getTitle(),review.getTitle());
        assertEquals(result.getBody().getSpoiler(),review.getSpoiler());
        assertEquals(result.getBody().getRating(),review.getRating());
        assertEquals(result.getBody().getBookNotion(),review.getBookNotion());
        assertEquals(result.getBody().getLastEditTime(),LocalDate.now());
    }
    @Test
    void updateNotOwnerOrAdmin() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.id(1L);
        review.userId(10L);
        when(communicationService.existsUser(9L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(true);
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.getOne(1L)).thenReturn(review);
        when(communicationService.isAdmin(9L)).thenReturn(false);
        assertThrows(CustomPermissionsException.class, () -> service.update(9L,review));
        verify(repository,never()).save(review);
        //assertEquals(result.getStatusCode(),HttpStatus.FORBIDDEN);
    }
    @Test
    void updateNotOwnerButAdmin() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(communicationService.existsUser(9L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(true);
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.getOne(1L)).thenReturn(review);
        when(communicationService.isAdmin(9L)).thenReturn(true);
        var result = service.update(9L,review);
        verify(repository).save(review);
        assertEquals(result.getBody(),review);
    }

    @Test
    void updateProfanities() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.id(1L);
        review.userId(10L);
        review.setText("fuck");
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.isAdmin(10L)).thenReturn(true);
        assertThrows(CustomProfanitiesException.class, () -> service.update(10L,review));
        verify(repository,never()).save(review);
    }

    @Test
    void updateInvalidUser() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.id(1L);
        review.userId(10L);
        review.setText("fuck");
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(communicationService.existsUser(10L)).thenReturn(false);
        when(communicationService.isAdmin(10L)).thenReturn(true);
        assertThrows(CustomBadRequestException.class, () -> service.update(10L,review));
        verify(repository,never()).save(review);
    }


    @Test
    void updateNull(){
        when(repository.existsById(1L)).thenReturn(true);
        assertThrows(CustomBadRequestException.class, () -> service.update(1L,null));
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class, () -> service.update(1L,null));

    }

    @Test
    void delete() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(repository).deleteById(1L);
        when(communicationService.isAdmin(10L)).thenReturn(true);
        var result = service.delete(1L,10L);
        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
        assertEquals(result.getStatusCode(),HttpStatus.OK);
    }

    @Test
    void deleteInvalid() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(false);
        when(repository.getOne(1L)).thenReturn(review);
        assertThrows(CustomBadRequestException.class, () -> service.delete(1L,10L));
        verify(repository).existsById(1L);
        verify(repository,never()).getOne(1L);
        verify(repository,never()).deleteById(1L);
    }


    @Test
    void deleteNotAdminOrOwner() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(communicationService.isAdmin(9L)).thenReturn(false);
        assertThrows(CustomPermissionsException.class, () -> service.delete(1L,9L));
    }

    @Test
    void deleteAdminButNotOwner() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(communicationService.isAdmin(10L)).thenReturn(true);
        var result = service.delete(1L,10L);
        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
        assertEquals(result.getStatusCode(),HttpStatus.OK);    }

    @Test
    void deleteOwnerButNotAdmin() {
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(communicationService.isAdmin(10L)).thenReturn(false);
        var result = service.delete(1L,10L);
        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
        assertEquals(result.getStatusCode(),HttpStatus.OK);    }


    @Test
    void testAddSpoiler() {
        Long reviewId = 1L;
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);

        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.getOne(1L)).thenReturn(review);

        ResponseEntity<String> response = service.addSpoiler(reviewId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(review.getSpoiler());
        verify(repository, times(1)).save(review);
    }




    @Test
    void testAddUpvote() {
        Long reviewId = 1L;
        Integer upvote = 1;
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.setUpvote(0L);
        review.setDownvote(0L);

        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.getOne(1L)).thenReturn(review);

        ResponseEntity<String> response = service.addVote(reviewId, upvote);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(review.getUpvote(), 1);
        verify(repository, times(1)).save(review);
    }

    @Test
    void addVoteInvalidId(){
        Long reviewId = 1L;
        Integer upvote = 1;
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.setUpvote(0L);
        review.setDownvote(0L);

        when(repository.existsById(reviewId)).thenReturn(false);

        ResponseEntity<String> response = service.addVote(reviewId, upvote);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(response.getBody(), "Review id does not exist.");
        verify(repository, times(0)).save(review);

    }

    @Test
    void addVoteInvalidVote(){
        Long reviewId = 1L;
        Integer upvote = 2;
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.setUpvote(0L);
        review.setDownvote(0L);

        when(repository.existsById(reviewId)).thenReturn(true);

        ResponseEntity<String> response = service.addVote(reviewId, upvote);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(response.getBody(), "The only accepted bodies are 0 for downvote and 1 for upvote.");
        verify(repository, times(0)).save(review);

    }


    @Test
    void testAddDownvote() {
        Long reviewId = 1L;
        Integer downvote = 0;
        Review review = new Review(1L,2L,10L, "Review", "review", 5L);
        review.setUpvote(0L);
        review.setDownvote(0L);
        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.getOne(1L)).thenReturn(review);

        ResponseEntity<String> response = service.addVote(reviewId, downvote);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(review.getUpvote(),0L);
        assertEquals(review.getDownvote(), 1);
        verify(repository, times(1)).save(review);
    }

    @Test
    void getFilterNull(){
        assertNull(service.getFilter("hello"));
    }

    @Test
    void seeAllMostRecent() {
        Review r = new Review(1L,2L,10L, "Review", "review", 5L);
        Review r2 = new Review(2L,2L,9L, "Review", "review", 5L);
        Review r3 = new Review(3L,1L,8L, "Review", "review", 5L);
        Review r4 = new Review(4L,2L,7L, "Review", "review", 5L);
        Review r5 = new Review(5L,2L,6L, "Review", "review", 5L);
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
        Review r = new Review(1L,2L,10L, "Review", "review", 5L);
        Review r2 = new Review(2L,2L,9L, "Review", "review", 5L);
        Review r3 = new Review(3L,1L,8L, "Review", "review", 5L);
        Review r4 = new Review(4L,2L,7L, "Review", "review", 5L);
        Review r5 = new Review(5L,2L,6L, "Review", "review", 5L);
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
        Review r = new Review(1L, 2L, 10L, "Review", "review", 5L);
        Review r2 = new Review(2L,2L,9L, "Review", "review", 5L);
        Review r3 = new Review(3L,1L,8L, "Review", "review", 5L);
        Review r4 = new Review(4L,2L,7L, "Review", "review", 5L);
        Review r5 = new Review(5L,2L,6L, "Review", "review", 5L);
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
        Review r1 = new Review(1L, 2L, userId, "Review", "review", 5L);
        Review r2 = new Review(2L, 2L, userId, "Review", "review", 5L);
        Review r3 = new Review(3L, 3L, userId, "Review", "review", 5L);
        Review r4 = new Review(4L, 5L, 89L, "Review", "review", 5L);
        Review r5 = new Review(5L, 5L, 78L, "Review", "review", 5L);
        Review r6 = new Review(6L, 2L, userId, "Review", "review", 5L);

        r1.setUpvote(0L);
        r2.setUpvote(0L);
        r3.setUpvote(0L);
        r4.setUpvote(0L);
        r5.setUpvote(0L);
        r6.setUpvote(0L);
        r1.setDownvote(0L);
        r2.setDownvote(0L);
        r3.setDownvote(0L);
        r4.setDownvote(0L);
        r5.setDownvote(0L);
        r6.setDownvote(0L);

        when(repository.findAll()).thenReturn(List.of(r1, r2, r3, r4, r5, r6));

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(r1));
        when(repository.getOne(1L)).thenReturn(r1);
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(2L)).thenReturn(true);
        when(repository.findById(2L)).thenReturn(Optional.of(r2));
        when(repository.getOne(2L)).thenReturn(r2);
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(3L)).thenReturn(true);
        when(repository.findById(3L)).thenReturn(Optional.of(r3));
        when(repository.getOne(3L)).thenReturn(r3);
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));


        when(repository.existsById(4L)).thenReturn(true);
        when(repository.findById(4L)).thenReturn(Optional.of(r4));
        when(repository.getOne(4L)).thenReturn(r4);
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(5L)).thenReturn(true);
        when(repository.findById(5L)).thenReturn(Optional.of(r5));
        when(repository.getOne(5L)).thenReturn(r5);
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(repository.existsById(6L)).thenReturn(true);
        when(repository.findById(6L)).thenReturn(Optional.of(r6));
        when(repository.getOne(6L)).thenReturn(r6);
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

    @Test
    void pinTest() {
        Long userId = 17L;
        Review r1 = new Review(1L, 2L, userId, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(r1));
        when(repository.getOne(1L)).thenReturn(r1);
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = service.pinReview(1L, true);
        System.out.println(response);
        assertTrue(r1.getPinned());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
    @Test
    void pinInvalid() {
        Long userId = 17L;
        Review r1 = new Review(1L, 2L, userId, "Review", "review", 5L);
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class, () -> service.pinReview(1L,true));
    }
}