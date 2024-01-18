package nl.tudelft.sem.template.review.textcheck;

import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.review.domain.textcheck.BaseTextHandler;
import nl.tudelft.sem.template.review.domain.textcheck.ProfanityHandler;
import nl.tudelft.sem.template.review.domain.textcheck.TextHandler;
import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.review.services.CommunicationServiceImpl;
import nl.tudelft.sem.template.review.services.GetReportServiceImpl;
import nl.tudelft.sem.template.review.services.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTextHandlerTest {
    private BaseTextHandler baseTextHandler;

    private ReviewServiceImpl service;

    @BeforeEach
    public void setup() {
        baseTextHandler =  mock(BaseTextHandler.class);
        ReviewRepository repository = mock(ReviewRepository.class);
        CommunicationServiceImpl communicationService = mock(CommunicationServiceImpl.class);
        GetReportServiceImpl getReportService = mock(GetReportServiceImpl.class);
        when(getReportService.addRatingAndNotion(any(),  any(),  any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));

        when(getReportService.removeRatingAndNotion(any(),  any(),  any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        when(getReportService.updateRatingAndNotion(any(),  any(),  any(),  any(),  any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        when(getReportService.createBookDataInRepository(any()))
                .thenReturn(ResponseEntity.of(Optional.of(new BookData(1L))));
        service = new ReviewServiceImpl(getReportService, repository, communicationService);

    }
    @Test
    void handleTextValid() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "review",  5L);

        service.handleText(review.getText());
        assertEquals(review.getText(), "review");
    }
    @Test
    void handleTextInvalid() {
        Review review = new Review(1L, 2L, 10L,  "Review",  "Bastard",  5L);

        assertThrows(CustomProfanitiesException.class,  () -> service.handleText(review.getText()));
        assertFalse(baseTextHandler.handle(review.getText()));
    }

    @Test
    void baseTextHandlerNullNext() {
        TextHandler baseTextHandler1 = new ProfanityHandler();
        baseTextHandler1.setNext(null);
        assertTrue(baseTextHandler1.handle(""));
    }

}
