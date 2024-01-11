package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReportReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ReportReviewServiceImplTest {

    private ReportReviewServiceImpl service;

    private ReportReviewRepository repository;

    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setup() {
        repository = mock(ReportReviewRepository.class);
        reviewRepository = mock(ReviewRepository.class);
        service = new ReportReviewServiceImpl(repository, reviewRepository);
    }

    @Test
    void report() {
        Review review = new Review(1L, 10L, 23L);

        when(reviewRepository.existsById(review.getId())).thenReturn(true);
        when(repository.save(ArgumentMatchers.any())).thenReturn(new ReportReview());

        when(reviewRepository.getOne(1L)).thenReturn(review);
        ResponseEntity<ReportReview> result = service.report(review.getId(),"foul language");

        verify(reviewRepository).existsById(review.getId());
        verify(reviewRepository).save(ArgumentMatchers.any());

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    @Test
    void reportInvalid() {
        ResponseEntity<ReportReview> result = service.report(1L,null);

        verify(repository, never()).save(any());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void get() {
        ReportReview reportReview = new ReportReview();
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(reportReview));

        ResponseEntity<ReportReview> result = service.get(1L);

        verify(repository).findById(1L);
        verify(repository).existsById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(reportReview, result.getBody());
    }

    @Test
    void getInvalid() {
        ResponseEntity<ReportReview> result = service.get(0L);

        verify(repository, never()).findById(0L);
        verify(repository).existsById(0L);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void getReportsForReview() {
        List<ReportReview> reports = Arrays.asList(new ReportReview(), new ReportReview());
        when(repository.findAllByReviewId(1L)).thenReturn(reports);

        ResponseEntity<List<ReportReview>> result = service.getReportsForReview(1L);

        verify(repository).findAllByReviewId(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(reports, result.getBody());
    }


    @Test
    void getAllReportedReviewsValid() {
        when(repository.findAll()).thenReturn(Arrays.asList(new ReportReview(), new ReportReview()));

        ResponseEntity<List<ReportReview>> result = service.getAllReportedReviews(1L);

        verify(repository).findAll();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void isReported() {
        when(repository.existsByReviewId(1L)).thenReturn(true);

        ResponseEntity<Boolean> result = service.isReported(1L);

        verify(repository).existsByReviewId(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
    }

    @Test
    void isNotReported() {
        ResponseEntity<Boolean> result = service.isReported(0L);

        verify(repository).existsByReviewId(0L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(result.getBody());
    }

    @Test
    void delete() {
        ReportReview reportReview = new ReportReview();
        Review rev = new Review(1L,2L,3L);
        reportReview.setReviewId(1L);
        rev.setReportList(new ArrayList<>());
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(reportReview));
        when(reviewRepository.getOne(1L)).thenReturn(rev);

        ResponseEntity<String> result = service.delete(1L, 1L);

        verify(repository).existsById(1L);
        //verify(repository).deleteById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void deleteInvalid() {
        ResponseEntity<String> result = service.delete(0L, 1L);

        verify(repository).existsById(0L);
        verify(repository, never()).deleteById(0L);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void deleteReportsForReview() {
        when(repository.existsByReviewId(1L)).thenReturn(true);
        when(repository.findAllByReviewId(1L)).thenReturn(Arrays.asList(new ReportReview()));
    }

    //getAllReportedCommentsInvalid, deleteReportsForCommentInvalid, deleteReportsForCommentNotAdmin
}