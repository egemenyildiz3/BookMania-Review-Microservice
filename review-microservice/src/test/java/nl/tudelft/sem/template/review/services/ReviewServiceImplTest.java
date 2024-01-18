package nl.tudelft.sem.template.review.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.review.domain.textcheck.BaseTextHandler;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.exceptions.CustomUserExistsException;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
class ReviewServiceImplTest {

    private ReviewServiceImpl service;
    private ReviewRepository repository;

    private CommunicationServiceImpl communicationService;
    private GetReportServiceImpl getReportService;


    @BeforeEach
    public void setup() {
        repository = mock(ReviewRepository.class);
        communicationService = mock(CommunicationServiceImpl.class);
         getReportService = mock(GetReportServiceImpl.class);
        when(getReportService.addRatingAndNotion(any(),  any(),  any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));

        when(getReportService.removeRatingAndNotion(any(),  any(),  any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        when(getReportService.updateRatingAndNotion(any(),  any(),  any(),  any(),  any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        when(getReportService.createBookDataInRepository(any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        service = new ReviewServiceImpl(getReportService,  repository,  communicationService);


    }

    @Test
    void add() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.save(review)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(true);
        var result = service.add(review);
        verify(repository).save(review);
        verify(getReportService).addRatingAndNotion(any(),any(),any());

        assertEquals(result.getBody(), review);
        assertEquals(Objects.requireNonNull(result.getBody()).getUpvote(), 0L);
        assertEquals(result.getBody().getDownvote(), 0L);
        assertEquals(result.getBody().getReportList().size(), 0);
        assertEquals(result.getBody().getCommentList().size(), 0);
        assertEquals(result.getBody().getId(), 0);

        Review r1 = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        r1.text("FUCK");
        when(repository.save(r1)).thenReturn(r1);
        assertThrows(CustomProfanitiesException.class,  () -> service.add(r1));
        verify(repository, never()).save(r1);

        Review r2 = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        r2.text("https://");
        when(repository.save(r2)).thenReturn(r2);
        assertThrows(CustomBadRequestException.class,  () -> service.add(r2));
        verify(repository, never()).save(r2);

        assertThrows(CustomBadRequestException.class,  () -> service.add(null));
        verify(repository, never()).save(r1);

    }

    @Test
    void addInvalidBook() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.save(review)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class,  () -> service.add(review));
        verify(repository, never()).save(review);

    }

    @Test
    void addInvalidUser() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.save(review)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(false);
        when(communicationService.existsBook(2L)).thenReturn(true);
        assertThrows(CustomUserExistsException.class, () -> service.add(review));
        verify(repository,never()).save(review);

    }

    @Test
    void getValid() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        var result = service.get(1L);
        verify(repository, times(2)).findById(1L);
        verify(repository).existsById(1L);
        assertEquals(result.getBody(), review);

    }

    @Test
    void getInvalid() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class,  () -> service.get(1L));
        verify(repository, never()).getOne(1L);
        verify(repository).existsById(1L);
    }

    @Test
    void updateOwnerNotAdmin() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.id(1L);
        review.userId(10L);
        review.setBookNotion(Review.BookNotionEnum.NEGATIVE);
        review.setSpoiler(false);
        Review review1 = new Review(1L, 2L, 10L,  "Rev",  "rev",  3L);
        review1.setBookNotion(Review.BookNotionEnum.NEUTRAL);
        review1.setSpoiler(true);
        when(repository.save(review1)).thenReturn(review1);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review1));
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.isAdmin(2L)).thenReturn(false);

        var result = service.update(10L, review);
        verify(repository).save(review1);
        verify(getReportService).updateRatingAndNotion(any(),any(),any(),any(),any());
        review.text("hahaha");
        result = service.update(10L, review);
        assertEquals(result.getBody(), review1);

        assertEquals(Objects.requireNonNull(result.getBody()).getText(), review.getText());
        assertEquals(result.getBody().getTitle(), review.getTitle());
        assertEquals(result.getBody().getSpoiler(), review.getSpoiler());
        assertEquals(result.getBody().getRating(), review.getRating());
        assertEquals(result.getBody().getBookNotion(), review.getBookNotion());
        assertEquals(result.getBody().getLastEditTime(), LocalDate.now());
    }

    @Test
    void updateNotOwnerOrAdmin() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.id(1L);
        review.userId(10L);
        when(communicationService.existsUser(9L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(true);
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.getOne(1L)).thenReturn(review);
        when(communicationService.isAdmin(9L)).thenReturn(false);
        assertThrows(CustomPermissionsException.class,  () -> service.update(9L, review));
        verify(repository, never()).save(review);
        //assertEquals(result.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    void updateNotOwnerButAdmin() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(communicationService.existsUser(9L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(true);
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(communicationService.isAdmin(9L)).thenReturn(true);
        var result = service.update(9L, review);
        review.setLastEditTime(LocalDate.now());
        verify(repository).save(review);
        assertEquals(result.getBody(), review);
    }

    @Test
    void updateProfanities() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.id(1L);
        review.userId(10L);
        review.setText("fuck");
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.getOne(1L)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.isAdmin(10L)).thenReturn(true);
        assertThrows(CustomProfanitiesException.class,  () -> service.update(10L, review));
        verify(repository, never()).save(review);
    }

    @Test
    void updateInvalidUser() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.id(1L);
        review.userId(10L);
        review.setText("fuck");
        when(repository.save(review)).thenReturn(review);
        when(repository.existsById(1L)).thenReturn(true);
        when(communicationService.existsUser(10L)).thenReturn(false);
        when(communicationService.isAdmin(10L)).thenReturn(true);
        assertThrows(CustomUserExistsException.class, () -> service.update(10L,review));
        verify(repository,never()).save(review);
    }

    @Test
    void updateNull() {
        when(repository.existsById(1L)).thenReturn(true);
        assertThrows(CustomBadRequestException.class,  () -> service.update(1L, null));
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class,  () -> service.update(1L, null));

    }

    @Test
    void delete() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        doNothing().when(repository).deleteById(1L);
        when(communicationService.isAdmin(10L)).thenReturn(true);
        var result = service.delete(1L, 10L);
        verify(repository,times(2)).findById(1L);
        verify(repository).deleteById(1L);
        verify(getReportService).removeRatingAndNotion(any(),any(),any());
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void addSecondReviewToBook() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.save(review)).thenReturn(review);
        when(communicationService.existsUser(10L)).thenReturn(true);
        when(communicationService.existsBook(2L)).thenReturn(true);
        when(repository.existsByBookIdAndUserId(2L,10L)).thenReturn(true);
        assertThrows(CustomBadRequestException.class,  () -> service.add(review));

    }

    @Test
    void deleteInvalid() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.existsById(1L)).thenReturn(false);
        when(repository.getOne(1L)).thenReturn(review);
        assertThrows(CustomBadRequestException.class,  () -> service.delete(1L, 10L));
        verify(repository).existsById(1L);
        verify(repository, never()).getOne(1L);
        verify(repository, never()).deleteById(1L);
    }


    @Test
    void deleteNotAdminOrOwner() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(communicationService.isAdmin(9L)).thenReturn(false);
        assertThrows(CustomPermissionsException.class,  () -> service.delete(1L, 9L));
    }

    @Test
    void deleteAdminButNotOwner() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(communicationService.isAdmin(10L)).thenReturn(true);
        var result = service.delete(1L, 10L);
        verify(repository,times(2)).findById(1L);
        verify(repository).deleteById(1L);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void deleteOwnerButNotAdmin() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(review));
        when(communicationService.isAdmin(10L)).thenReturn(false);
        var result = service.delete(1L, 10L);
        verify(repository,times(2)).findById(1L);
        verify(repository).deleteById(1L);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testAddSpoiler() {
        Long reviewId = 1L;
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);

        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.getOne(1L)).thenReturn(review);

        ResponseEntity<String> response = service.addSpoiler(reviewId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(review.getSpoiler());
        verify(repository,  times(1)).save(review);
    }

    @Test
    void testAddUpvote() {
        Long reviewId = 1L;
        Integer upvote = 1;
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.setUpvote(0L);
        review.setDownvote(0L);

        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.getOne(1L)).thenReturn(review);

        ResponseEntity<String> response = service.addVote(reviewId,  upvote);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(review.getUpvote(),  1);
        verify(repository,  times(1)).save(review);
    }

    @Test
    void addVoteInvalidId() {
        Long reviewId = 1L;
        Integer upvote = 1;
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.setUpvote(0L);
        review.setDownvote(0L);

        when(repository.existsById(reviewId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.addVote(reviewId,upvote));


        verify(repository, times(0)).save(review);

    }

    @Test
    void addVoteInvalidVote() {
        Long reviewId = 1L;
        Integer upvote = 2;
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.setUpvote(0L);
        review.setDownvote(0L);

        when(repository.existsById(reviewId)).thenReturn(true);

        assertThrows(CustomBadRequestException.class, () -> service.addVote(reviewId,upvote));


        verify(repository, times(0)).save(review);

    }


    @Test
    void testAddDownvote() {
        Long reviewId = 1L;
        Integer downvote = 0;
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        review.setUpvote(0L);
        review.setDownvote(0L);
        when(repository.existsById(reviewId)).thenReturn(true);
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.getOne(1L)).thenReturn(review);

        ResponseEntity<String> response = service.addVote(reviewId,  downvote);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(review.getUpvote(), 0L);
        assertEquals(review.getDownvote(),  1);
        verify(repository,  times(1)).save(review);
    }

    @Test
    void seeAllMostRecent() {
        Review r = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        Review r2 = new Review(2L, 2L, 9L,  "Review",  "review",  5L);
        Review r3 = new Review(3L, 1L, 8L,  "Review",  "review",  5L);
        Review r4 = new Review(4L, 2L, 7L,  "Review",  "review",  5L);
        Review r5 = new Review(5L, 2L, 6L,  "Review",  "review",  5L);
        when(repository.findAll()).thenReturn(List.of(r, r2, r3, r4, r5));

        r.setTimeCreated(LocalDate.of(2003, 12, 27));
        r2.setTimeCreated(LocalDate.of(2020, 9, 27));
        r3.setTimeCreated(LocalDate.of(2020, 9, 30));
        r4.setTimeCreated(LocalDate.of(2019, 1, 27));
        r5.setTimeCreated(LocalDate.of(2020, 9, 26));

        List<Review> correctList = List.of(r2, r5, r4, r);

        ResponseEntity<List<Review>> reviews = service.seeAll(2L,  "mostRecent");
        assertEquals(reviews.getBody(),  correctList);
    }

    @Test
    void seeAllMostUpvote() {
        Review r = new Review(1L, 2L, 10L,  "Review",  "review",  5L);
        Review r2 = new Review(2L, 2L, 9L,  "Review",  "review",  5L);
        Review r3 = new Review(3L, 1L, 8L,  "Review",  "review",  5L);
        Review r4 = new Review(4L, 2L, 7L,  "Review",  "review",  5L);
        Review r5 = new Review(5L, 2L, 6L,  "Review",  "review",  5L);
        when(repository.findAll()).thenReturn(List.of(r, r2, r3, r4, r5));

        r.setUpvote(9999L);
        r2.setUpvote(0L);
        r3.setUpvote(9999L);
        r4.setUpvote(1L);
        r5.setUpvote(10000L);



        List<Review> correctList = List.of(r5, r, r4, r2);

        ResponseEntity<List<Review>> reviews = service.seeAll(2L,  "highestRated");
        assertEquals(reviews.getBody(),  correctList);
    }

    @Test
    void seeAllMostRelevant() {
        Review r = new Review(1L,  2L,  10L,  "Review",  "review",  5L);
        Review r2 = new Review(2L, 2L, 9L,  "Review",  "review",  5L);
        Review r3 = new Review(3L, 1L, 8L,  "Review",  "review",  5L);
        Review r4 = new Review(4L, 2L, 7L,  "Review",  "review",  5L);
        Review r5 = new Review(5L, 2L, 6L,  "Review",  "review",  5L);
        when(repository.findAll()).thenReturn(List.of(r, r2, r3, r4, r5));

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

        List<Review> correctList = List.of(r, r5, r4, r2);

        ResponseEntity<List<Review>> reviews = service.seeAll(2L,  "mostRelevant");
        assertEquals(reviews.getBody(),  correctList);
    }

    @Test
    void seeAllPinnedTest() {
        Review r1 = new Review(1L,  1L,  10L,  "Review",  "review",  5L);
        Review r2 = new Review(2L, 1L, 9L,  "Review",  "review",  5L);
        Review r3 = new Review(3L, 1L, 8L,  "Review",  "review",  5L);
        Review r4 = new Review(4L, 1L, 7L,  "Review",  "review",  5L);
        Review r5 = new Review(5L, 1L, 6L,  "Review",  "review",  5L);

        r1.setPinned(Boolean.TRUE);
        r3.setPinned(Boolean.FALSE);
        r4.setPinned(Boolean.TRUE);
        r5.setPinned(Boolean.TRUE);

        r1.setTimeCreated(LocalDate.of(2003, 12, 27));
        r2.setTimeCreated(LocalDate.of(2020, 9, 27));
        r3.setTimeCreated(LocalDate.of(2020, 9, 30));
        r4.setTimeCreated(LocalDate.of(2019, 1, 27));
        r5.setTimeCreated(LocalDate.of(2020, 9, 26));

        when(repository.findAll()).thenReturn(List.of(r1, r2, r3, r4, r5));
        List<Review> correctList = List.of(r5, r4, r1, r3, r2);

        ResponseEntity<List<Review>> reviews = service.seeAll(1L,  "mostRecent");
        assertEquals(reviews.getBody(),  correctList);
    }

    @Test
    void retrieveMostUpvotedTest() {
        Long userId = 17L;
        Review r1 = new Review(1L,  2L,  userId,  "Review",  "review",  5L);
        Review r3 = new Review(3L,  3L,  userId,  "Review",  "review",  5L);
        Review r6 = new Review(6L,  2L,  userId,  "Review",  "review",  5L);

        r1.setUpvote(5L);
        r3.setUpvote(3L);

        r6.setUpvote(4L);
        r1.setDownvote(0L);

        r3.setDownvote(0L);
        r6.setDownvote(0L);



        List<Review> corrList = new ArrayList<>();
        corrList.add(r1);
        corrList.add(r6);
        corrList.add(r3);
        when(repository.findTop3ByUserIdOrderByUpvoteDesc(userId)).thenReturn(corrList);
        when(communicationService.existsUser(userId)).thenReturn(true);
        assertEquals(service.mostUpvotedReviews(userId).getBody(),  corrList);

        when(communicationService.existsUser(userId)).thenReturn(false);
        assertThrows(CustomUserExistsException.class, () -> service.mostUpvotedReviews(2L));
        verify(repository,never()).findTop3ByUserIdOrderByUpvoteDesc(2L);


    }

    @Test
    void pinTest() {
        Long userId = 17L;
        Review r1 = new Review(1L,  2L,  userId,  "Review",  "review",  5L);
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(r1));
        when(repository.getOne(1L)).thenReturn(r1);
        when(repository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<String> response = service.pinReview(1L,  true);
        System.out.println(response);
        assertTrue(r1.getPinned());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void pinInvalid() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(CustomBadRequestException.class,  () -> service.pinReview(1L, true));
    }
}