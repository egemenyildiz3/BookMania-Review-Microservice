package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.model.ReportReview;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface ReportReviewService {
    ResponseEntity<ReportReview> report(Review review);

    ResponseEntity<ReportReview> get(Long id);

    ResponseEntity<List<ReportReview>> getReportsForReview(Long reviewId);

    ResponseEntity<List<ReportReview>> getAllReportedReviews(Long reviewId);
    ResponseEntity<Boolean> isReported(Long reviewId);

    ResponseEntity<String> delete(Long id, Long userId);

    ResponseEntity<String> deleteReportsForReview(Long reviewId, Long userId);
}

