package nl.tudelft.sem.template.review.services;

import nl.tudelft.sem.template.review.exceptions.CustomBadRequestException;
import nl.tudelft.sem.template.review.exceptions.CustomPermissionsException;
import nl.tudelft.sem.template.review.exceptions.CustomProfanitiesException;
import nl.tudelft.sem.template.review.repositories.ReportReviewRepository;
import nl.tudelft.sem.template.review.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.review.services.ReportReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ReportReviewServiceImplTest {

    private ReportReviewServiceImpl service;

    private ReportReviewRepository repository;

    private CommunicationServiceImpl communicationService;

    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setup() {
        repository = mock(ReportReviewRepository.class);
        communicationService = mock(CommunicationServiceImpl.class);
        reviewRepository = mock(ReviewRepository.class);
        service = new ReportReviewServiceImpl(repository, communicationService, reviewRepository);
    }

    @Test
    void reportValid() {
        long validReviewId = 1L;
        String validReason = "Inappropriate content";
        Review mockReview = new Review();
        mockReview.setId(validReviewId);
        mockReview.setText("This is a valid review text.");
        mockReview.setUserId(123L);

        when(reviewRepository.existsById(validReviewId)).thenReturn(true);
        when(reviewRepository.getOne(validReviewId)).thenReturn(mockReview);
        when(communicationService.existsUser(mockReview.getUserId())).thenReturn(true);

        ResponseEntity<ReportReview> result = service.report(validReviewId, validReason);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(validReason, result.getBody().getReason());
        assertEquals(validReviewId, result.getBody().getReviewId());
    }

    @Test
    void report_NullReviewId_ThrowsBadRequestException() {
        long invalidReviewId = 999L;
        long validReviewId = 1L;
        String validReason = "Inappropriate content";
        Review mockReview = new Review();
        mockReview.setId(validReviewId);
        mockReview.setUserId(123L);

        when(reviewRepository.existsById(validReviewId)).thenReturn(true);
        when(reviewRepository.getOne(validReviewId)).thenReturn(mockReview);
        when(communicationService.existsUser(mockReview.getUserId())).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.report(validReviewId, validReason));

        when(reviewRepository.existsById(invalidReviewId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.report(invalidReviewId, "Reason"));
        assertThrows(CustomBadRequestException.class, () -> service.report(null, "Reason"));
        assertThrows(CustomBadRequestException.class, () -> service.report(1L, null));
    }


    @Test
    void getValid() {
        long validReportId = 1L;
        ReportReview mockReportReview = new ReportReview();
        mockReportReview.setId(validReportId);

        when(repository.existsById(validReportId)).thenReturn(true);
        when(repository.findById(validReportId)).thenReturn(Optional.of(mockReportReview));

        ResponseEntity<ReportReview> result = service.get(validReportId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(validReportId, result.getBody().getId());
    }

    @Test
    void getInvalidThrowsBadRequestException() {
        long invalidReportId = 999L;
        long validReviewId = 1L;
        ReportReview mockReportReview = new ReportReview();
        mockReportReview.setReviewId(validReviewId);

        when(repository.existsById(invalidReportId)).thenReturn(false);
        when(reviewRepository.existsById(validReviewId)).thenReturn(true);
        when(repository.findAllByReviewId(validReviewId)).thenReturn(Collections.singletonList(mockReportReview));

        ResponseEntity<List<ReportReview>> result = service.getReportsForReview(validReviewId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(validReviewId, result.getBody().get(0).getReviewId());
        assertThrows(CustomBadRequestException.class, () -> service.get(invalidReportId));
    }

    @Test
    void getReportsForReviewInvalidThrowsBadRequestException() {
        long invalidReviewId = 999L;

        when(reviewRepository.existsById(invalidReviewId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.getReportsForReview(invalidReviewId));
    }

    @Test
    void getAllReportedReviewsValid() {
        long adminUserId = 123L;

        when(communicationService.isAdmin(adminUserId)).thenReturn(true);
        when(repository.findAll()).thenReturn(Collections.singletonList(new ReportReview()));

        ResponseEntity<List<ReportReview>> result = service.getAllReportedReviews(adminUserId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
    }

    @Test
    void getAllReportedReviewsInvalidThrowsPermissionsException() {
        long nonAdminUserId = 456L;

        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.getAllReportedReviews(nonAdminUserId));
    }

    @Test
    void isReportedValid() {
        long validReviewId = 1L;

        when(reviewRepository.existsById(validReviewId)).thenReturn(true);
        when(repository.existsByReviewId(validReviewId)).thenReturn(true);

        ResponseEntity<Boolean> result = service.isReported(validReviewId);

        assertEquals(200, result.getStatusCodeValue());
        assertNotNull(result.getBody());
        assertTrue(result.getBody());
    }

    @Test
    void isReportedInvalidThrowsBadRequestException() {
        long invalidReviewId = 999L;

        when(reviewRepository.existsById(invalidReviewId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.isReported(invalidReviewId));
    }

    @Test
    void deleteValid() {
        long validReportId = 1L;
        long adminUserId = 123L;
        ReportReview mockReportReview = new ReportReview();
        mockReportReview.setId(validReportId);
        mockReportReview.setReviewId(456L);
        Review rev = new Review();
        rev.setReportList(new ArrayList<>());
        when(repository.existsById(validReportId)).thenReturn(true);
        when(repository.findById(validReportId)).thenReturn(Optional.of(mockReportReview));
        when(communicationService.isAdmin(adminUserId)).thenReturn(true);
        when(reviewRepository.getOne(mockReportReview.getReviewId())).thenReturn(rev);

        ResponseEntity<String> result = service.delete(validReportId, adminUserId);

        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void deleteInvalidThrowsPermissionsException() {
        long validReportId = 1L;
        long nonAdminUserId = 456L;

        when(repository.existsById(validReportId)).thenReturn(true);
        when(repository.findById(validReportId)).thenReturn(Optional.of(new ReportReview()));

        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.delete(validReportId, nonAdminUserId));
    }

    @Test
    void deleteInvalidThrowsBadRequestException() {
        long invalidReportId = 999L;
        long adminUserId = 123L;

        when(repository.existsById(invalidReportId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.delete(invalidReportId, adminUserId));
    }


    @Test
    void deleteReportsForReviewValid() {
        long validReviewId = 1L;
        long adminUserId = 123L;
        Review rev = new Review();
        rev.setReportList(new ArrayList<>());
        when(reviewRepository.existsById(validReviewId)).thenReturn(true);
        when(communicationService.isAdmin(adminUserId)).thenReturn(true);
        when(repository.findAllByReviewId(validReviewId)).thenReturn(Collections.singletonList(new ReportReview(1L,validReviewId,"reason")));
        when(reviewRepository.getOne(validReviewId)).thenReturn(rev);

        ResponseEntity<String> result = service.deleteReportsForReview(validReviewId, adminUserId);

        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void deleteReportsForReviewInvalidThrowsPermissionsException() {
        long validReviewId = 1L;
        long nonAdminUserId = 456L;

        when(reviewRepository.existsById(validReviewId)).thenReturn(true);
        when(communicationService.isAdmin(nonAdminUserId)).thenReturn(false);

        assertThrows(CustomPermissionsException.class, () -> service.deleteReportsForReview(validReviewId, nonAdminUserId));
    }

    @Test
    void deleteReportsForReviewInvalidThrowsBadRequestException() {
        long invalidReviewId = 999L;
        long adminUserId = 123L;

        when(reviewRepository.existsById(invalidReviewId)).thenReturn(false);

        assertThrows(CustomBadRequestException.class, () -> service.deleteReportsForReview(invalidReviewId, adminUserId));
    }
}