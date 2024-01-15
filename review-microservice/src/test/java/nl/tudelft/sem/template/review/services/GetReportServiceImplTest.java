package nl.tudelft.sem.template.review.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomUserExistsException;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

class GetReportServiceImplTest {


    private GetReportServiceImpl service;
    private BookDataRepository bookDataRepository;
    private ReviewRepository reviewRepository;
    private CommunicationServiceImpl communicationService;
    private CommentService commentService;

    @BeforeEach
    void setup() {
        this.bookDataRepository = mock(BookDataRepository.class);
        this.reviewRepository = mock(ReviewRepository.class);
        this.communicationService = mock(CommunicationServiceImpl.class);
        this.commentService = mock(CommentService.class);
        this.service = new GetReportServiceImpl(this.bookDataRepository, this.reviewRepository,
                this.communicationService, commentService);

        when(communicationService.existsUser(any(Long.class))).thenReturn(true);
        when(communicationService.existsBook(any(Long.class))).thenReturn(true);
    }

    @Test
    void getReportDoesntExist() {
        long id = 10L;
        BookData expected = new BookData(id);
        expected.setAvrRating(0.0);
        expected.setPositiveRev(0);
        expected.setNeutralRev(0);
        expected.setNegativeRev(0);

        when(bookDataRepository.save(expected)).thenReturn(expected);

        BookData result = service.getReport(id, 5L, "report").getBody();

        assertEquals(expected, result);
    }

    @Test
    void getReportNullUser() {
        assertThrows(CustomBadRequestException.class, () -> service.getReport(10L, null, "report"));
    }

    @Test
    void getReportNullBook() {
        assertThrows(CustomBadRequestException.class, () -> service.getReport(null, 5L, "report"));
    }

    @Test
    void getReportNullInfo() {
        assertThrows(CustomBadRequestException.class, () -> service.getReport(10L, 5L, null));
    }

    @Test
    void getReportUserDoesntExist() {
        Long user = 10L;
        when(communicationService.existsUser(any(Long.class))).thenReturn(false);
        when(communicationService.existsUser(user)).thenReturn(false);
        assertThrows(CustomUserExistsException.class, () -> service.getReport(10L, 5L, "report"));
    }

    @Test
    void getReportBookDoesntExist() {
        Long book = 20L;
        when(communicationService.existsBook(any(Long.class))).thenReturn(false);
        when(communicationService.existsUser(book)).thenReturn(false);
        assertThrows(CustomBadRequestException.class, () -> service.getReport(book, 5L, "report"));
    }

    @Test
    void testGetReportReport() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        List<Long> reviews = new ArrayList<>();
        Review mostUpvoted = new Review(5L, id, 20L, "Review", "text", 5L);
        reviews.add(mostUpvoted.getId());
        when(reviewRepository.findMostUpvotedReviewId(eq(id), any(Pageable.class))).thenReturn(reviews);
        when(commentService.findMostUpvotedComment(id)).thenReturn(ResponseEntity.badRequest().build());

        BookData result = service.getReport(id, 5L, "report").getBody();

        data.setMostUpvotedReview(mostUpvoted.getId());

        assertEquals(data, result);
    }

    @Test
    void testGetReportReportNoReviews() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        List<Long> reviews = new ArrayList<>();
        when(reviewRepository.findMostUpvotedReviewId(eq(id), any(Pageable.class))).thenReturn(reviews);
        when(commentService.findMostUpvotedComment(id)).thenReturn(ResponseEntity.badRequest().build());
        BookData result = service.getReport(id, 5L, "report").getBody();

        assertEquals(data, result);
    }

    @Test
    void testGetReportRating() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        BookData result = service.getReport(id, 5L, "rating").getBody();

        assertNotNull(result);
        assertEquals(data.getAvrRating(), result.getAvrRating());
    }

    @Test
    void testGetReportInteraction() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        BookData result = service.getReport(id, 5L, "interactions").getBody();

        assertEquals(data, result);
    }

    @Test
    void getReportWrongInfo() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        assertThrows(CustomBadRequestException.class, () -> service.getReport(id, 5L, "bla"));
    }

    @Test
    void addRatingAndNotionPositive() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookData result = service.addRatingAndNotion(id, 3L, Review.BookNotionEnum.POSITIVE).getBody();

        data.setPositiveRev(4);
        data.setAvrRating((4.0 * 5 + 3) / 6);

        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void addRatingAndNotionNeutral() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookData result = service.addRatingAndNotion(id, 3L, Review.BookNotionEnum.NEUTRAL).getBody();

        data.setNeutralRev(2);
        data.setAvrRating((4.0 * 5 + 3) / 6);

        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void addRatingAndNotionNegative() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookData result = service.addRatingAndNotion(id, 3L, Review.BookNotionEnum.NEGATIVE).getBody();

        data.setNegativeRev(2);
        data.setAvrRating((4.0 * 5 + 3) / 6);

        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void addRatingAndNotionBookDataDoesntExist() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(false);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookData result = service.addRatingAndNotion(id, 3L, Review.BookNotionEnum.POSITIVE).getBody();

        data.setPositiveRev(4);
        data.setAvrRating((4.0 * 5 + 3) / 6);

        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void addRatingAndNotionBookDataNullId() {
        when(bookDataRepository.existsById(any(Long.class))).thenReturn(false);

        assertThrows(CustomBadRequestException.class,
                () -> service.addRatingAndNotion(null, 3L, Review.BookNotionEnum.POSITIVE));
    }

    @Test
    void removeRatingAndNotion() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookData result = service.removeRatingAndNotion(id, 2L, Review.BookNotionEnum.NEUTRAL).getBody();

        data.setNeutralRev(0);
        data.setAvrRating((4.0 * 5 - 2) / 4);

        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void removeRatingAndNotionPositive() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookData result = service.removeRatingAndNotion(id, 2L, Review.BookNotionEnum.POSITIVE).getBody();

        data.setPositiveRev(2);
        data.setAvrRating((4.0 * 5 - 2) / 4);

        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void removeRatingAndNotionBookDataDoesntExist() {
        long id = 20L;

        when(bookDataRepository.existsById(id)).thenReturn(false);
        assertThrows(CustomBadRequestException.class,
                () -> service.removeRatingAndNotion(id, 2L, Review.BookNotionEnum.NEUTRAL));
    }

    @Test
    void updateRatingAndNotion() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final BookData result = service.updateRatingAndNotion(id, 3L, Review.BookNotionEnum.NEGATIVE,
                5L, Review.BookNotionEnum.POSITIVE).getBody();
        // Kinda bad testing here, but I didn't know how to do this as there is no direct information interchange
        data.setNegativeRev(0);
        data.setAvrRating((4.0 * 5 - 3) / 4);
        verify(bookDataRepository).save(data);

        data.setNegativeRev(1);
        data.setPositiveRev(4);
        data.setAvrRating((4.0 * 5 + 5) / 6);

        assertNotNull(result);
        assertEquals(data, result);
    }

    @Test
    void updateRatingAndNotionNull() {
        assertThrows(CustomBadRequestException.class,
                () -> service.updateRatingAndNotion(null, 3L, Review.BookNotionEnum.NEGATIVE,
                        5L, Review.BookNotionEnum.POSITIVE));
    }

    @Test
    void createBookDataInRepository() {
        long id = 10L;
        BookData expected = new BookData(id);
        expected.setAvrRating(0.0);
        expected.setPositiveRev(0);
        expected.setNeutralRev(0);
        expected.setNegativeRev(0);

        when(bookDataRepository.save(expected)).thenReturn(expected);

        BookData result = service.createBookDataInRepository(id).getBody();

        assertEquals(expected, result);
    }

    @Test
    void createBookDataInRepositoryAlreadyExists() {
        long id = 10L;
        when(bookDataRepository.existsById(id)).thenReturn(true);

        assertThrows(CustomBadRequestException.class, () -> service.createBookDataInRepository(id));
    }
}