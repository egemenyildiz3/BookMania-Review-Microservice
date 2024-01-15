package nl.tudelft.sem.template.review.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.model.BookData;
import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.repositories.BookDataRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

class StatsServiceImplTest {

    private StatsService service;
    private BookDataRepository bookDataRepository;
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setup() {
        bookDataRepository = mock(BookDataRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        this.service = new StatsServiceImpl(bookDataRepository, reviewRepository);
    }


    @Test
    void avgRating() {
        BookData bookData = new BookData(2L);
        bookData.setAvrRating(22.0 / 6.0);

        when(bookDataRepository.findById(2L)).thenReturn(Optional.of(bookData));
        when(bookDataRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(CustomBadRequestException.class, () -> service.avgRating(3L));
        assertEquals(22.0 / 6.0, service.avgRating(2L).getBody());


    }

    @Test
    void interactions() {
        when(bookDataRepository.existsById(3L)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.interactions(3L));

        when(reviewRepository.countByBookId(2L)).thenReturn(10L);
        when(bookDataRepository.existsById(2L)).thenReturn(true);
        assertEquals(10L, service.interactions(2L).getBody());

    }
}