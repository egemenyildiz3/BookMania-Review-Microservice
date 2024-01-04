package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.BookDataRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GetReportServiceImplTest {


    private GetReportServiceImpl service;
    private BookDataRepository bookDataRepository;
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setup() {
        this.bookDataRepository = mock(BookDataRepository.class);
        this.reviewRepository = mock(ReviewRepository.class);
        this.service = new GetReportServiceImpl(this.bookDataRepository, this.reviewRepository);
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

        BookData result = service.getReport(id, "5", "report").getBody();

        assertEquals(expected, result);
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
        Review mostUpvoted = new Review(5L, id, 20L);
        reviews.add(mostUpvoted.getId());
        when(reviewRepository.findMostUpvotedReviewId(eq(id), any(Pageable.class))).thenReturn(reviews);

        BookData result = service.getReport(id, "5", "report").getBody();

        data.setMostUpvotedReview(mostUpvoted.getId());

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

        BookData result = service.getReport(id, "5", "rating").getBody();

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

        BookData result = service.getReport(id, "5", "report").getBody();

        assertEquals(data, result);
    }

    @Test
    void addRatingAndNotion() {
        long id = 20L;
        BookData data = new BookData(id);
        data.setAvrRating(4.0);
        data.setPositiveRev(3);
        data.setNeutralRev(1);
        data.setNegativeRev(1);

        when(bookDataRepository.existsById(id)).thenReturn(true);
        when(bookDataRepository.getOne(id)).thenReturn(data);

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation ->  {return invocation.getArgument(0);});

        BookData result = service.addRatingAndNotion(id, 3L, Review.BookNotionEnum.POSITIVE).getBody();

        data.setPositiveRev(4);
        data.setAvrRating((4.0 * 5 + 3) / 6);

        assertNotNull(result);
        assertEquals(data, result);
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

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation ->  {return invocation.getArgument(0);});

        BookData result = service.removeRatingAndNotion(id, 2L, Review.BookNotionEnum.NEUTRAL).getBody();

        data.setNeutralRev(0);
        data.setAvrRating((4.0 * 5 - 2) / 4);

        assertNotNull(result);
        assertEquals(data, result);
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

        when(bookDataRepository.save(any(BookData.class))).thenAnswer(invocation ->  {return invocation.getArgument(0);});

        BookData result = service.updateRatingAndNotion(id, 3L, Review.BookNotionEnum.NEGATIVE
        , 5L, Review.BookNotionEnum.POSITIVE).getBody();
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
}